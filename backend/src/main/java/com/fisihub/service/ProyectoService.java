package com.fisihub.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Comparator;

import com.fisihub.dto.ActividadResponse;
import com.fisihub.dto.MiembroProyectoResponse;
import com.fisihub.dto.ProyectoDetalleResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fisihub.dto.ProyectoRequest;
import com.fisihub.dto.ProyectoMiembrosResponse;
import com.fisihub.dto.ProyectoResponse;
import com.fisihub.dto.ProyectoTareaResumenResponse;
import com.fisihub.dto.TareaResponse;
import com.fisihub.exception.BusinessRuleException;
import com.fisihub.exception.ForbiddenOperationException;
import com.fisihub.exception.ResourceNotFoundException;
import com.fisihub.model.EstadoProyecto;
import com.fisihub.model.EstadoTarea;
import com.fisihub.model.EspacioTrabajo;
import com.fisihub.model.PrioridadProyecto;
import com.fisihub.model.PrioridadTarea;
import com.fisihub.model.Proyecto;
import com.fisihub.model.Tarea;
import com.fisihub.model.RolProyecto;
import com.fisihub.model.Usuario;
import com.fisihub.repository.ProyectoRepository;
import com.fisihub.repository.TareaRepository;

@Service
public class ProyectoService {

    private final ProyectoRepository proyectoRepository;
    private final EspacioService espacioService;
    private final UsuarioService usuarioService;
    private final HistorialActividadService historialService;
    private final ProyectoPermisoService permisoService;
    private final EspacioPermisoService espacioPermisoService;
    private final TareaRepository tareaRepository;
    private final MiembroProyectoService miembroProyectoService;

    public ProyectoService(
            ProyectoRepository proyectoRepository,
            EspacioService espacioService,
            UsuarioService usuarioService,
            HistorialActividadService historialService,
            ProyectoPermisoService permisoService,
            EspacioPermisoService espacioPermisoService,
            TareaRepository tareaRepository,
            MiembroProyectoService miembroProyectoService) {
        this.proyectoRepository = proyectoRepository;
        this.espacioService = espacioService;
        this.usuarioService = usuarioService;
        this.historialService = historialService;
        this.permisoService = permisoService;
        this.espacioPermisoService = espacioPermisoService;
        this.tareaRepository = tareaRepository;
        this.miembroProyectoService = miembroProyectoService;
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

    @Transactional(readOnly = true)
    public ProyectoDetalleResponse obtenerDetalle(Long id, String correo) {
        Proyecto proyecto = buscarAccesible(id, correo);
        List<TareaResponse> tareas = tareaRepository.findByProyectoIdOrderByCreadoEnDesc(id)
                .stream()
                .map(tarea -> toTareaResponse(tarea, correo))
                .toList();
        List<ActividadResponse> actividad = historialService.listarPorProyecto(
                id,
                correo);
        ProyectoMiembrosResponse miembros = miembroProyectoService.listar(
                id,
                correo);

        long pendientes = contarPorEstado(tareas, EstadoTarea.PENDIENTE);
        long enProceso = contarPorEstado(tareas, EstadoTarea.EN_PROCESO);
        long enRevision = contarPorEstado(tareas, EstadoTarea.EN_REVISION);
        long bloqueadas = contarPorEstado(tareas, EstadoTarea.BLOQUEADA);
        long completadas = contarPorEstado(tareas, EstadoTarea.COMPLETADA);
        long vencidas = tareas.stream()
                .filter(this::estaVencida)
                .count();

        List<TareaResponse> tareasDestacadas = tareas.stream()
                .filter(tarea -> tarea.estado() != EstadoTarea.COMPLETADA)
                .sorted(Comparator
                        .comparing((TareaResponse tarea) -> !estaVencida(tarea))
                        .thenComparing(tarea -> tarea.fechaLimite() == null
                                ? LocalDate.MAX
                                : tarea.fechaLimite())
                        .thenComparing(tarea -> prioridadOrden(tarea.prioridad())))
                .limit(6)
                .toList();

        return new ProyectoDetalleResponse(
                toResponse(proyecto, correo),
                new ProyectoTareaResumenResponse(
                        tareas.size(),
                        pendientes,
                        enProceso,
                        enRevision,
                        bloqueadas,
                        completadas,
                        vencidas),
                tareasDestacadas,
                miembros,
                actividad.stream().limit(8).toList());
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

    private long contarPorEstado(List<TareaResponse> tareas, EstadoTarea estado) {
        return tareas.stream().filter(tarea -> tarea.estado() == estado).count();
    }

    private boolean estaVencida(TareaResponse tarea) {
        return tarea.fechaLimite() != null
                && tarea.fechaLimite().isBefore(LocalDate.now())
                && tarea.estado() != EstadoTarea.COMPLETADA;
    }

    private int prioridadOrden(PrioridadTarea prioridad) {
        return switch (prioridad) {
            case URGENTE -> 0;
            case ALTA -> 1;
            case MEDIA -> 2;
            case BAJA -> 3;
        };
    }

    private TareaResponse toTareaResponse(Tarea tarea, String correo) {
        boolean puedeGestionar = permisoService.puedeGestionar(
                tarea.getProyecto(),
                correo);
        boolean esResponsable = tarea.getResponsable() != null
                && tarea.getResponsable().getCorreo().equalsIgnoreCase(correo);
        return new TareaResponse(
                tarea.getId(),
                tarea.getTitulo(),
                tarea.getDescripcion(),
                tarea.getProyecto().getId(),
                tarea.getProyecto().getNombre(),
                tarea.getResponsable() == null ? null : tarea.getResponsable().getId(),
                tarea.getResponsable() == null ? null : tarea.getResponsable().getNombre(),
                tarea.getFechaLimite(),
                tarea.getEstado(),
                tarea.getPrioridad(),
                tarea.getCreadoPor().getId(),
                tarea.getCreadoPor().getNombre(),
                tarea.getCreadoEn(),
                tarea.getActualizadoEn(),
                puedeGestionar || esResponsable,
                puedeGestionar,
                puedeGestionar,
                puedeGestionar || esResponsable);
    }

    private String normalizarOpcional(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
