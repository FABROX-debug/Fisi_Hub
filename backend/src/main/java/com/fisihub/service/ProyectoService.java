package com.fisihub.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fisihub.dto.ProyectoRequest;
import com.fisihub.dto.ProyectoResponse;
import com.fisihub.exception.BusinessRuleException;
import com.fisihub.exception.ForbiddenOperationException;
import com.fisihub.exception.ResourceNotFoundException;
import com.fisihub.model.EstadoProyecto;
import com.fisihub.model.EspacioTrabajo;
import com.fisihub.model.PrioridadProyecto;
import com.fisihub.model.Proyecto;
import com.fisihub.model.RolProyecto;
import com.fisihub.model.Usuario;
import com.fisihub.repository.ProyectoRepository;

@Service
public class ProyectoService {

    private final ProyectoRepository proyectoRepository;
    private final EspacioService espacioService;
    private final UsuarioService usuarioService;
    private final HistorialActividadService historialService;
    private final ProyectoPermisoService permisoService;
    private final EspacioPermisoService espacioPermisoService;

    public ProyectoService(
            ProyectoRepository proyectoRepository,
            EspacioService espacioService,
            UsuarioService usuarioService,
            HistorialActividadService historialService,
            ProyectoPermisoService permisoService,
            EspacioPermisoService espacioPermisoService) {
        this.proyectoRepository = proyectoRepository;
        this.espacioService = espacioService;
        this.usuarioService = usuarioService;
        this.historialService = historialService;
        this.permisoService = permisoService;
        this.espacioPermisoService = espacioPermisoService;
    }

    @Transactional
    public ProyectoResponse crear(ProyectoRequest request, String correo) {
        validarFechas(request.fechaInicio(), request.fechaFin());
        EspacioTrabajo espacio = espacioService.buscarAccesible(
                request.espacioId(),
                correo);
        if (!espacioPermisoService.puedeGestionar(espacio, correo)) {
            throw new ForbiddenOperationException(
                    "Solo un lider del espacio o un administrador puede crear proyectos");
        }
        Usuario usuario = usuarioService.buscarPorCorreo(correo);
        Proyecto proyecto = new Proyecto(
                request.nombre().trim(),
                normalizarOpcional(request.descripcion()),
                request.fechaInicio(),
                request.fechaFin(),
                request.estado() == null
                        ? EstadoProyecto.PLANIFICADO
                        : request.estado(),
                request.prioridad() == null
                        ? PrioridadProyecto.MEDIA
                        : request.prioridad(),
                espacio,
                usuario);
        proyecto.agregarMiembro(usuario, RolProyecto.LIDER);
        Proyecto guardado = proyectoRepository.save(proyecto);
        historialService.registrar(
                guardado,
                usuario,
                com.fisihub.model.TipoActividad.PROYECTO_CREADO,
                usuario.getNombre() + " creo el proyecto \""
                        + guardado.getNombre() + "\"");
        return toResponse(guardado, correo);
    }

    @Transactional(readOnly = true)
    public List<ProyectoResponse> listar(String correo) {
        return proyectoRepository
                .findDistinctByMiembrosUsuarioCorreoIgnoreCaseOrderByCreadoEnDesc(
                        correo)
                .stream()
                .map(proyecto -> toResponse(proyecto, correo))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ProyectoResponse> listarPorEspacio(
            Long espacioId,
            String correo) {
        espacioService.buscarAccesible(espacioId, correo);
        return proyectoRepository
                .findDistinctByEspacioIdAndMiembrosUsuarioCorreoIgnoreCaseOrderByCreadoEnDesc(
                        espacioId,
                        correo)
                .stream()
                .map(proyecto -> toResponse(proyecto, correo))
                .toList();
    }

    @Transactional(readOnly = true)
    public ProyectoResponse obtener(Long id, String correo) {
        return toResponse(buscarAccesible(id, correo), correo);
    }

    @Transactional
    public ProyectoResponse actualizar(
            Long id,
            ProyectoRequest request,
            String correo) {
        Proyecto proyecto = buscarAccesible(id, correo);
        validarLider(proyecto, correo);
        if (!proyecto.getEspacio().getId().equals(request.espacioId())) {
            throw new BusinessRuleException(
                    "No se puede mover el proyecto a otro espacio");
        }
        validarFechas(request.fechaInicio(), request.fechaFin());
        proyecto.actualizar(
                request.nombre().trim(),
                normalizarOpcional(request.descripcion()),
                request.fechaInicio(),
                request.fechaFin(),
                request.estado() == null
                        ? proyecto.getEstado()
                        : request.estado(),
                request.prioridad() == null
                        ? proyecto.getPrioridad()
                        : request.prioridad());
        return toResponse(proyecto, correo);
    }

    @Transactional
    public void eliminar(Long id, String correo) {
        Proyecto proyecto = buscarAccesible(id, correo);
        validarLider(proyecto, correo);
        proyectoRepository.delete(proyecto);
    }

    @Transactional(readOnly = true)
    public Proyecto buscarAccesible(Long id, String correo) {
        if (usuarioService.esAdmin(correo)) {
            return proyectoRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Proyecto no encontrado"));
        }
        return proyectoRepository
                .findDistinctByIdAndMiembrosUsuarioCorreoIgnoreCase(id, correo)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Proyecto no encontrado"));
    }

    public ProyectoResponse toResponse(
            Proyecto proyecto,
            String correo) {
        return new ProyectoResponse(
                proyecto.getId(),
                proyecto.getNombre(),
                proyecto.getDescripcion(),
                proyecto.getFechaInicio(),
                proyecto.getFechaFin(),
                proyecto.getEstado(),
                proyecto.getPrioridad(),
                proyecto.getPorcentajeAvance(),
                proyecto.getEspacio().getId(),
                proyecto.getEspacio().getNombre(),
                proyecto.getLider().getId(),
                proyecto.getLider().getNombre(),
                proyecto.getMiembros().size(),
                proyecto.getCreadoEn(),
                permisoService.puedeGestionar(proyecto, correo));
    }

    private void validarLider(Proyecto proyecto, String correo) {
        if (!permisoService.puedeGestionar(proyecto, correo)) {
            throw new ForbiddenOperationException(
                    "Solo un lider del proyecto o un administrador puede modificarlo");
        }
    }

    private void validarFechas(LocalDate inicio, LocalDate fin) {
        if (inicio != null && fin != null && fin.isBefore(inicio)) {
            throw new BusinessRuleException(
                    "La fecha de fin no puede ser anterior a la fecha de inicio");
        }
    }

    private String normalizarOpcional(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
