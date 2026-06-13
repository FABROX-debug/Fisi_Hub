package com.fisihub.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fisihub.dto.MiembroProyectoRequest;
import com.fisihub.dto.MiembroProyectoResponse;
import com.fisihub.dto.ProyectoMiembrosResponse;
import com.fisihub.dto.RolProyectoRequest;
import com.fisihub.exception.BusinessRuleException;
import com.fisihub.exception.ConflictException;
import com.fisihub.exception.ForbiddenOperationException;
import com.fisihub.exception.ResourceNotFoundException;
import com.fisihub.model.EstadoTarea;
import com.fisihub.model.MiembroProyecto;
import com.fisihub.model.Proyecto;
import com.fisihub.model.RolProyecto;
import com.fisihub.model.TipoActividad;
import com.fisihub.model.Usuario;
import com.fisihub.repository.MiembroProyectoRepository;
import com.fisihub.repository.ProyectoRepository;
import com.fisihub.repository.TareaRepository;

@Service
public class MiembroProyectoService {

    private final MiembroProyectoRepository miembroRepository;
    private final ProyectoRepository proyectoRepository;
    private final TareaRepository tareaRepository;
    private final UsuarioService usuarioService;
    private final HistorialActividadService historialService;

    public MiembroProyectoService(
            MiembroProyectoRepository miembroRepository,
            ProyectoRepository proyectoRepository,
            TareaRepository tareaRepository,
            UsuarioService usuarioService,
            HistorialActividadService historialService) {
        this.miembroRepository = miembroRepository;
        this.proyectoRepository = proyectoRepository;
        this.tareaRepository = tareaRepository;
        this.usuarioService = usuarioService;
        this.historialService = historialService;
    }

    @Transactional(readOnly = true)
    public ProyectoMiembrosResponse listar(Long proyectoId, String correo) {
        Proyecto proyecto = buscarParaConsulta(proyectoId, correo);
        boolean puedeGestionar = puedeGestionar(proyecto, correo);
        return new ProyectoMiembrosResponse(
                proyecto.getId(),
                proyecto.getNombre(),
                puedeGestionar,
                miembroRepository.findByProyectoIdOrderByUsuarioNombreAsc(
                                proyectoId)
                        .stream()
                        .map(miembro -> toResponse(miembro, proyecto))
                        .toList());
    }

    @Transactional
    public MiembroProyectoResponse agregar(
            Long proyectoId,
            MiembroProyectoRequest request,
            String correo) {
        Proyecto proyecto = buscarParaGestion(proyectoId, correo);
        Usuario usuario = usuarioService.buscarPorCorreo(request.correo());
        if (!usuario.isActivo()) {
            throw new BusinessRuleException("El usuario esta inactivo");
        }
        if (miembroRepository.existsByProyectoIdAndUsuarioId(
                proyectoId,
                usuario.getId())) {
            throw new ConflictException("El usuario ya pertenece al proyecto");
        }
        RolProyecto rol = request.rol() == null
                ? RolProyecto.MIEMBRO
                : request.rol();
        MiembroProyecto miembro = miembroRepository.save(
                new MiembroProyecto(proyecto, usuario, rol));
        Usuario actor = usuarioService.buscarPorCorreo(correo);
        historialService.registrar(
                proyecto,
                actor,
                TipoActividad.MIEMBRO_AGREGADO,
                actor.getNombre() + " agrego a " + usuario.getNombre()
                        + " al proyecto");
        return toResponse(miembro, proyecto);
    }

    @Transactional
    public MiembroProyectoResponse cambiarRol(
            Long proyectoId,
            Long usuarioId,
            RolProyectoRequest request,
            String correo) {
        Proyecto proyecto = buscarParaGestion(proyectoId, correo);
        MiembroProyecto miembro = buscarMiembro(proyectoId, usuarioId);
        if (miembro.getRol() == RolProyecto.LIDER
                && request.rol() == RolProyecto.MIEMBRO) {
            validarNoEsUltimoLider(proyectoId);
        }
        miembro.cambiarRol(request.rol());
        return toResponse(miembro, proyecto);
    }

    @Transactional
    public void quitar(Long proyectoId, Long usuarioId, String correo) {
        Proyecto proyecto = buscarParaGestion(proyectoId, correo);
        MiembroProyecto miembro = buscarMiembro(proyectoId, usuarioId);
        if (proyecto.getLider().getId().equals(usuarioId)) {
            throw new BusinessRuleException(
                    "No se puede quitar al lider designado del proyecto");
        }
        if (miembro.getRol() == RolProyecto.LIDER) {
            validarNoEsUltimoLider(proyectoId);
        }
        if (tareaRepository.countByProyectoIdAndResponsableId(
                proyectoId,
                usuarioId) > 0) {
            throw new BusinessRuleException(
                    "No se puede quitar un miembro con tareas asignadas");
        }
        proyecto.quitarMiembro(miembro);
        miembroRepository.delete(miembro);
        miembroRepository.flush();
    }

    private Proyecto buscarParaConsulta(Long proyectoId, String correo) {
        if (usuarioService.esAdmin(correo)) {
            return proyectoRepository.findById(proyectoId)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Proyecto no encontrado"));
        }
        return proyectoRepository
                .findDistinctByIdAndMiembrosUsuarioCorreoIgnoreCase(
                        proyectoId,
                        correo)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Proyecto no encontrado"));
    }

    private Proyecto buscarParaGestion(Long proyectoId, String correo) {
        Proyecto proyecto = buscarParaConsulta(proyectoId, correo);
        if (!puedeGestionar(proyecto, correo)) {
            throw new ForbiddenOperationException(
                    "Solo el lider del proyecto o un administrador puede gestionar miembros");
        }
        return proyecto;
    }

    private boolean puedeGestionar(Proyecto proyecto, String correo) {
        return proyecto.getLider().getCorreo().equalsIgnoreCase(correo)
                || miembroRepository
                        .findByProyectoIdAndUsuarioCorreoIgnoreCase(
                                proyecto.getId(),
                                correo)
                        .map(miembro -> miembro.getRol() == RolProyecto.LIDER)
                        .orElse(false)
                || usuarioService.esAdmin(correo);
    }

    private MiembroProyecto buscarMiembro(Long proyectoId, Long usuarioId) {
        return miembroRepository.findByProyectoIdAndUsuarioId(
                        proyectoId,
                        usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Miembro del proyecto no encontrado"));
    }

    private void validarNoEsUltimoLider(Long proyectoId) {
        if (miembroRepository.countByProyectoIdAndRol(
                proyectoId,
                RolProyecto.LIDER) <= 1) {
            throw new BusinessRuleException(
                    "El proyecto debe conservar al menos un lider");
        }
    }

    private MiembroProyectoResponse toResponse(
            MiembroProyecto miembro,
            Proyecto proyecto) {
        long tareasActivas = tareaRepository
                .countByProyectoIdAndResponsableIdAndEstadoNot(
                        proyecto.getId(),
                        miembro.getUsuario().getId(),
                        EstadoTarea.COMPLETADA);
        return new MiembroProyectoResponse(
                miembro.getUsuario().getId(),
                miembro.getUsuario().getNombre(),
                miembro.getUsuario().getCorreo(),
                miembro.getRol(),
                tareasActivas,
                proyecto.getLider().getId().equals(
                        miembro.getUsuario().getId()));
    }
}
