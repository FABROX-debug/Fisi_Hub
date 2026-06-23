package com.fisihub.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fisihub.dto.EspacioEquipoResponse;
import com.fisihub.dto.EspacioMiembroResponse;
import com.fisihub.dto.RolEspacioRequest;
import com.fisihub.exception.BusinessRuleException;
import com.fisihub.exception.ForbiddenOperationException;
import com.fisihub.exception.ResourceNotFoundException;
import com.fisihub.model.EspacioMiembro;
import com.fisihub.model.EspacioTrabajo;
import com.fisihub.model.EstadoTarea;
import com.fisihub.model.RolEspacio;
import com.fisihub.repository.EspacioMiembroRepository;
import com.fisihub.repository.MiembroProyectoRepository;
import com.fisihub.repository.TareaRepository;

@Service
public class EspacioMiembroService {

    private final EspacioService espacioService;
    private final EspacioPermisoService permisoService;
    private final EspacioMiembroRepository miembroRepository;
    private final MiembroProyectoRepository miembroProyectoRepository;
    private final TareaRepository tareaRepository;

    public EspacioMiembroService(
            EspacioService espacioService,
            EspacioPermisoService permisoService,
            EspacioMiembroRepository miembroRepository,
            MiembroProyectoRepository miembroProyectoRepository,
            TareaRepository tareaRepository) {
        this.espacioService = espacioService;
        this.permisoService = permisoService;
        this.miembroRepository = miembroRepository;
        this.miembroProyectoRepository = miembroProyectoRepository;
        this.tareaRepository = tareaRepository;
    }

    @Transactional(readOnly = true)
    public EspacioEquipoResponse listar(Long espacioId, String correo) {
        EspacioTrabajo espacio = espacioService.buscarAccesible(
                espacioId,
                correo);
        return new EspacioEquipoResponse(
                espacio.getId(),
                espacio.getNombre(),
                permisoService.puedeGestionar(espacio, correo),
                miembroRepository.findByEspacioIdOrderByUsuarioNombreAsc(
                                espacioId)
                        .stream()
                        .map(miembro -> toResponse(miembro, espacioId))
                        .toList());
    }

    @Transactional
    public EspacioMiembroResponse cambiarRol(
            Long espacioId,
            Long usuarioId,
            RolEspacioRequest request,
            String correo) {
        EspacioTrabajo espacio = buscarParaGestion(espacioId, correo);
        EspacioMiembro miembro = buscarMiembro(espacioId, usuarioId);
        if (miembro.getRol() == RolEspacio.LIDER
                && request.rol() == RolEspacio.MIEMBRO
                && miembroRepository.countByEspacioIdAndRol(
                        espacioId,
                        RolEspacio.LIDER) <= 1) {
            throw new BusinessRuleException(
                    "El espacio debe conservar al menos un lider");
        }
        miembro.cambiarRol(request.rol());
        return toResponse(miembro, espacio.getId());
    }

    @Transactional
    public void quitar(Long espacioId, Long usuarioId, String correo) {
        EspacioTrabajo espacio = buscarParaGestion(espacioId, correo);
        EspacioMiembro miembro = buscarMiembro(espacioId, usuarioId);
        if (espacio.getCreadoPor().getId().equals(usuarioId)) {
            throw new BusinessRuleException(
                    "No se puede quitar al creador del espacio");
        }
        if (miembro.getRol() == RolEspacio.LIDER
                && miembroRepository.countByEspacioIdAndRol(
                        espacioId,
                        RolEspacio.LIDER) <= 1) {
            throw new BusinessRuleException(
                    "El espacio debe conservar al menos un lider");
        }
        if (miembroProyectoRepository.existsByProyectoEspacioIdAndUsuarioId(
                espacioId,
                usuarioId)) {
            throw new BusinessRuleException(
                    "Quita primero al usuario de los proyectos del espacio");
        }
        espacio.quitarMiembro(miembro);
        miembroRepository.delete(miembro);
    }

    private EspacioTrabajo buscarParaGestion(Long espacioId, String correo) {
        EspacioTrabajo espacio = espacioService.buscarAccesible(
                espacioId,
                correo);
        if (!permisoService.puedeGestionar(espacio, correo)) {
            throw new ForbiddenOperationException(
                    "Solo un lider del espacio o un administrador puede gestionar el equipo");
        }
        return espacio;
    }

    private EspacioMiembro buscarMiembro(Long espacioId, Long usuarioId) {
        return miembroRepository.findByEspacioIdAndUsuarioId(
                        espacioId,
                        usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Miembro del espacio no encontrado"));
    }

    private EspacioMiembroResponse toResponse(
            EspacioMiembro miembro,
            Long espacioId) {
        return new EspacioMiembroResponse(
                miembro.getUsuario().getId(),
                miembro.getUsuario().getNombre(),
                miembro.getUsuario().getCorreo(),
                miembro.getRol(),
                miembro.getUsuario().isActivo(),
                tareaRepository
                        .countByProyectoEspacioIdAndResponsableIdAndEstadoNot(
                                espacioId,
                                miembro.getUsuario().getId(),
                                EstadoTarea.COMPLETADA));
    }
}
