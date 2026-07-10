package com.fisihub.service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fisihub.dto.EstadoTareaRequest;
import com.fisihub.dto.ActividadResponse;
import com.fisihub.dto.ComentarioResponse;
import com.fisihub.dto.MiTrabajoProyectoResponse;
import com.fisihub.dto.MiTrabajoResponse;
import com.fisihub.dto.MiTrabajoResumenResponse;
import com.fisihub.dto.TareaAlertasResponse;
import com.fisihub.dto.TareaDetalleProyectoResponse;
import com.fisihub.dto.TareaDetalleResponse;
import com.fisihub.dto.TareaCreateRequest;
import com.fisihub.dto.TareaResponse;
import com.fisihub.dto.TareaUpdateRequest;
import com.fisihub.exception.BusinessRuleException;
import com.fisihub.exception.ForbiddenOperationException;
import com.fisihub.exception.ResourceNotFoundException;
import com.fisihub.model.EstadoTarea;
import com.fisihub.model.Comentario;
import com.fisihub.model.MiembroProyecto;
import com.fisihub.model.PrioridadTarea;
import com.fisihub.model.Proyecto;
import com.fisihub.model.Tarea;
import com.fisihub.model.Usuario;
import com.fisihub.repository.MiembroProyectoRepository;
import com.fisihub.repository.ComentarioRepository;
import com.fisihub.repository.ProyectoRepository;
import com.fisihub.repository.TareaRepository;

@Service
public class TareaService {

    private final TareaRepository tareaRepository;
    private final MiembroProyectoRepository miembroProyectoRepository;
    private final ProyectoRepository proyectoRepository;
    private final ProyectoService proyectoService;
    private final UsuarioService usuarioService;
    private final HistorialActividadService historialService;
    private final ComentarioRepository comentarioRepository;
    private final NotificacionService notificacionService;
    private final ProyectoPermisoService permisoService;

    public TareaService(
            TareaRepository tareaRepository,
            MiembroProyectoRepository miembroProyectoRepository,
            ProyectoRepository proyectoRepository,
            ProyectoService proyectoService,
            UsuarioService usuarioService,
            HistorialActividadService historialService,
            ComentarioRepository comentarioRepository,
            NotificacionService notificacionService,
            ProyectoPermisoService permisoService) {
        this.tareaRepository = tareaRepository;
        this.miembroProyectoRepository = miembroProyectoRepository;
        this.proyectoRepository = proyectoRepository;
        this.proyectoService = proyectoService;
        this.usuarioService = usuarioService;
        this.historialService = historialService;
        this.comentarioRepository = comentarioRepository;
        this.notificacionService = notificacionService;
        this.permisoService = permisoService;
    }

    @Transactional
    public TareaResponse crear(TareaCreateRequest request, String correo) {
        Proyecto proyecto = proyectoService.buscarAccesible(
                request.proyectoId(),
                correo);
        Usuario creador = usuarioService.buscarPorCorreo(correo);
        Usuario responsable = buscarResponsable(
                proyecto.getId(),
                request.responsableId());
        if (!permisoService.puedeGestionar(proyecto, correo)
                && responsable != null
                && !responsable.getCorreo().equalsIgnoreCase(correo)) {
            throw new ForbiddenOperationException(
                    "Un miembro solo puede asignarse a si mismo al crear una tarea");
        }

        Tarea tarea = new Tarea(
                request.titulo().trim(),
                normalizarOpcional(request.descripcion()),
                proyecto,
                responsable,
                request.fechaLimite(),
                request.estado() == null
                        ? EstadoTarea.PENDIENTE
                        : request.estado(),
                request.prioridad() == null
                        ? PrioridadTarea.MEDIA
                        : request.prioridad(),
                creador);
        Tarea guardada = tareaRepository.saveAndFlush(tarea);
        recalcularAvance(proyecto);
        historialService.registrar(
                proyecto,
                creador,
                com.fisihub.model.TipoActividad.TAREA_CREADA,
                creador.getNombre() + " creo la tarea \""
                        + guardada.getTitulo() + "\"");
        notificacionService.notificarAsignacion(guardada);
        return toResponse(guardada, correo);
    }

    @Transactional(readOnly = true)
    public List<TareaResponse> listar(
            String correo,
            EstadoTarea estado,
            PrioridadTarea prioridad,
            Long proyectoId,
            Long responsableId) {
        List<Tarea> tareas;
        if (usuarioService.esAdmin(correo)) {
            tareas = proyectoId == null
                    ? tareaRepository.findAllByOrderByCreadoEnDesc()
                    : tareaRepository.findByProyectoIdOrderByCreadoEnDesc(
                            proyectoId);
        } else {
            tareas = proyectoId == null
                    ? tareaRepository
                            .findDistinctByProyectoMiembrosUsuarioCorreoIgnoreCaseOrderByCreadoEnDesc(
                                    correo)
                    : tareaRepository
                            .findDistinctByProyectoIdAndProyectoMiembrosUsuarioCorreoIgnoreCaseOrderByCreadoEnDesc(
                                    proyectoId,
                                    correo);
        }

        return tareas.stream()
                .filter(tarea -> estado == null || tarea.getEstado() == estado)
                .filter(tarea -> prioridad == null
                        || tarea.getPrioridad() == prioridad)
                .filter(tarea -> responsableId == null
                        || tarea.getResponsable() != null
                        && tarea.getResponsable().getId().equals(responsableId))
                .map(tarea -> toResponse(tarea, correo))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TareaResponse> listarPorProyecto(
            Long proyectoId,
            String correo) {
        proyectoService.buscarAccesible(proyectoId, correo);
        List<Tarea> tareas = usuarioService.esAdmin(correo)
                ? tareaRepository.findByProyectoIdOrderByCreadoEnDesc(
                        proyectoId)
                : tareaRepository
                        .findDistinctByProyectoIdAndProyectoMiembrosUsuarioCorreoIgnoreCaseOrderByCreadoEnDesc(
                                proyectoId,
                                correo);
        return tareas.stream()
                .map(tarea -> toResponse(tarea, correo))
                .toList();
    }

    @Transactional(readOnly = true)
    public TareaResponse obtener(Long id, String correo) {
        return toResponse(buscarAccesible(id, correo), correo);
    }

    @Transactional(readOnly = true)
    public TareaDetalleResponse obtenerDetalle(Long id, String correo) {
        Tarea tarea = buscarAccesible(id, correo);
        LocalDate hoy = LocalDate.now();
        List<ComentarioResponse> comentarios = comentarioRepository
                .findByTareaIdOrderByCreadoEnAsc(tarea.getId())
                .stream()
                .map(comentario -> toComentarioResponse(comentario, tarea, correo))
                .toList();
        List<ActividadResponse> actividad = historialService
                .listarPorProyecto(tarea.getProyecto().getId(), correo)
                .stream()
                .filter(item -> actividadRelacionadaConTarea(item, tarea))
                .limit(8)
                .toList();

        return new TareaDetalleResponse(
                toResponse(tarea, correo),
                new TareaDetalleProyectoResponse(
                        tarea.getProyecto().getId(),
                        tarea.getProyecto().getNombre(),
                        tarea.getProyecto().getEspacio().getNombre(),
                        tarea.getProyecto().getEstado(),
                        tarea.getProyecto().getPorcentajeAvance()),
                comentarios,
                actividad,
                buildAlertas(tarea, hoy));
    }

    @Transactional(readOnly = true)
    public MiTrabajoResponse obtenerMiTrabajo(String correo) {
        LocalDate hoy = LocalDate.now();
        List<Tarea> tareasAsignadas = tareaRepository
                .findByResponsableCorreoIgnoreCaseOrderByCreadoEnDesc(correo);
        List<Proyecto> proyectosParticipando = proyectoRepository
                .findDistinctByMiembrosUsuarioCorreoIgnoreCaseOrderByCreadoEnDesc(
                        correo);
        Set<Long> proyectoIds = proyectosParticipando.stream()
                .map(Proyecto::getId)
                .collect(java.util.stream.Collectors.toSet());

        List<Tarea> tareasVisibles = mergeTasks(
                tareasAsignadas,
                tareaRepository
                        .findDistinctByProyectoMiembrosUsuarioCorreoIgnoreCaseOrderByCreadoEnDesc(
                                correo));

        List<TareaResponse> tareasAsignadasResponse = tareasAsignadas.stream()
                .sorted(taskPriorityComparator(hoy))
                .map(tarea -> toResponse(tarea, correo))
                .toList();

        List<TareaResponse> tareasPrioritarias = tareasAsignadas.stream()
                .filter(tarea -> tarea.getEstado() != EstadoTarea.COMPLETADA)
                .sorted(taskPriorityComparator(hoy))
                .limit(6)
                .map(tarea -> toResponse(tarea, correo))
                .toList();

        List<TareaResponse> tareasNecesitanAccion = tareasAsignadas.stream()
                .filter(tarea -> requiereAccion(tarea, hoy))
                .sorted(taskPriorityComparator(hoy))
                .map(tarea -> toResponse(tarea, correo))
                .toList();

        List<MiTrabajoProyectoResponse> proyectosConCarga = proyectosParticipando
                .stream()
                .map(proyecto -> new MiTrabajoProyectoResponse(
                        proyecto.getId(),
                        proyecto.getNombre(),
                        proyecto.getEspacio().getNombre(),
                        proyecto.getEstado(),
                        proyecto.getPrioridad(),
                        proyecto.getPorcentajeAvance(),
                        proyecto.getFechaFin(),
                        proyecto.getLider().getNombre(),
                        tareasAsignadas.stream()
                                .filter(tarea -> tarea.getProyecto().getId()
                                        .equals(proyecto.getId()))
                                .filter(tarea -> tarea.getEstado() != EstadoTarea.COMPLETADA)
                                .count()))
                .filter(proyecto -> proyecto.tareasActivas() > 0
                        || proyectoIds.contains(proyecto.id())
                                && tareasVisibles.stream().anyMatch(
                                        tarea -> tarea.getProyecto().getId()
                                                .equals(proyecto.id())))
                .sorted(Comparator
                        .comparingLong(MiTrabajoProyectoResponse::tareasActivas)
                        .reversed()
                        .thenComparing(MiTrabajoProyectoResponse::fechaFin,
                                Comparator.nullsLast(Comparator.naturalOrder())))
                .limit(6)
                .toList();

        MiTrabajoResumenResponse resumen = new MiTrabajoResumenResponse(
                countByState(tareasAsignadas, EstadoTarea.PENDIENTE),
                countByState(tareasAsignadas, EstadoTarea.EN_PROCESO),
                countByState(tareasAsignadas, EstadoTarea.EN_REVISION),
                countByState(tareasAsignadas, EstadoTarea.BLOQUEADA),
                countByState(tareasAsignadas, EstadoTarea.COMPLETADA),
                tareasAsignadas.stream().filter(tarea -> isOverdue(tarea, hoy)).count(),
                tareasAsignadas.stream().filter(tarea -> isDueToday(tarea, hoy)).count());

        return new MiTrabajoResponse(
                resumen,
                tareasPrioritarias,
                tareasAsignadasResponse,
                tareasNecesitanAccion,
                proyectosConCarga);
    }

    @Transactional
    public TareaResponse actualizar(
            Long id,
            TareaUpdateRequest request,
            String correo) {
        Tarea tarea = buscarAccesible(id, correo);
        boolean puedeGestionar = permisoService.puedeGestionar(
                tarea.getProyecto(),
                correo);
        validarPuedeModificar(tarea, correo, puedeGestionar);
        Long responsableAnterior = tarea.getResponsable() == null
                ? null
                : tarea.getResponsable().getId();
        if (!puedeGestionar
                && !Objects.equals(
                        responsableAnterior,
                        request.responsableId())) {
            throw new ForbiddenOperationException(
                    "Solo un lider o administrador puede reasignar tareas");
        }
        Usuario responsable = buscarResponsable(
                tarea.getProyecto().getId(),
                request.responsableId());
        tarea.actualizar(
                request.titulo().trim(),
                normalizarOpcional(request.descripcion()),
                responsable,
                request.fechaLimite(),
                request.estado() == null ? tarea.getEstado() : request.estado(),
                request.prioridad() == null
                        ? tarea.getPrioridad()
                        : request.prioridad());
        tareaRepository.flush();
        recalcularAvance(tarea.getProyecto());
        if (responsable != null
                && !responsable.getId().equals(responsableAnterior)) {
            notificacionService.notificarAsignacion(tarea);
        }
        return toResponse(tarea, correo);
    }

    @Transactional
    public TareaResponse cambiarEstado(
            Long id,
            EstadoTareaRequest request,
            String correo) {
        Tarea tarea = buscarAccesible(id, correo);
        validarPuedeModificar(
                tarea,
                correo,
                permisoService.puedeGestionar(tarea.getProyecto(), correo));
        EstadoTarea estadoAnterior = tarea.getEstado();
        tarea.cambiarEstado(request.estado());
        tareaRepository.flush();
        recalcularAvance(tarea.getProyecto());
        Usuario actor = usuarioService.buscarPorCorreo(correo);
        historialService.registrar(
                tarea.getProyecto(),
                actor,
                com.fisihub.model.TipoActividad.ESTADO_TAREA_CAMBIADO,
                actor.getNombre() + " cambio \"" + tarea.getTitulo()
                        + "\" de " + estadoAnterior + " a " + request.estado());
        return toResponse(tarea, correo);
    }

    @Transactional
    public void eliminar(Long id, String correo) {
        Tarea tarea = buscarAccesible(id, correo);
        if (!permisoService.puedeGestionar(tarea.getProyecto(), correo)) {
            throw new ForbiddenOperationException(
                    "Solo un lider del proyecto o un administrador puede eliminar tareas");
        }
        Proyecto proyecto = tarea.getProyecto();
        tareaRepository.delete(tarea);
        tareaRepository.flush();
        recalcularAvance(proyecto);
    }

    @Transactional(readOnly = true)
    public Tarea buscarAccesible(Long id, String correo) {
        if (usuarioService.esAdmin(correo)) {
            return tareaRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Tarea no encontrada"));
        }
        return tareaRepository
                .findDistinctByIdAndProyectoMiembrosUsuarioCorreoIgnoreCase(
                        id,
                        correo)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Tarea no encontrada"));
    }

    private Usuario buscarResponsable(Long proyectoId, Long responsableId) {
        if (responsableId == null) {
            return null;
        }
        MiembroProyecto miembro = miembroProyectoRepository
                .findByProyectoIdAndUsuarioId(proyectoId, responsableId)
                .orElseThrow(() -> new BusinessRuleException(
                        "El responsable debe ser miembro del proyecto"));
        if (!miembro.getUsuario().isActivo()) {
            throw new BusinessRuleException(
                    "No se puede asignar una tarea a un usuario inactivo");
        }
        return miembro.getUsuario();
    }

    private void recalcularAvance(Proyecto proyecto) {
        long total = tareaRepository.countByProyectoId(proyecto.getId());
        long completadas = tareaRepository.countByProyectoIdAndEstado(
                proyecto.getId(),
                EstadoTarea.COMPLETADA);
        int porcentaje = total == 0
                ? 0
                : (int) Math.round(completadas * 100.0 / total);
        proyecto.actualizarPorcentajeAvance(porcentaje);
    }

    private TareaResponse toResponse(Tarea tarea, String correo) {
        Usuario responsable = tarea.getResponsable();
        boolean puedeGestionar = permisoService.puedeGestionar(
                tarea.getProyecto(),
                correo);
        boolean esResponsable = responsable != null
                && responsable.getCorreo().equalsIgnoreCase(correo);
        return new TareaResponse(
                tarea.getId(),
                tarea.getTitulo(),
                tarea.getDescripcion(),
                tarea.getProyecto().getId(),
                tarea.getProyecto().getNombre(),
                responsable == null ? null : responsable.getId(),
                responsable == null ? null : responsable.getNombre(),
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

    private void validarPuedeModificar(
            Tarea tarea,
            String correo,
            boolean puedeGestionar) {
        boolean esResponsable = tarea.getResponsable() != null
                && tarea.getResponsable().getCorreo()
                        .equalsIgnoreCase(correo);
        if (!puedeGestionar && !esResponsable) {
            throw new ForbiddenOperationException(
                    "Solo el responsable, un lider o un administrador puede modificar la tarea");
        }
    }

    private String normalizarOpcional(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private List<Tarea> mergeTasks(List<Tarea> primary, List<Tarea> secondary) {
        LinkedHashMap<Long, Tarea> merged = new LinkedHashMap<>();
        primary.forEach(tarea -> merged.put(tarea.getId(), tarea));
        secondary.forEach(tarea -> merged.putIfAbsent(tarea.getId(), tarea));
        return List.copyOf(merged.values());
    }

    private long countByState(List<Tarea> tareas, EstadoTarea estado) {
        return tareas.stream().filter(tarea -> tarea.getEstado() == estado).count();
    }

    private boolean requiereAccion(Tarea tarea, LocalDate hoy) {
        return tarea.getEstado() == EstadoTarea.BLOQUEADA
                || tarea.getEstado() == EstadoTarea.EN_REVISION
                || isDueToday(tarea, hoy)
                || isOverdue(tarea, hoy);
    }

    private boolean isDueToday(Tarea tarea, LocalDate hoy) {
        return tarea.getFechaLimite() != null
                && tarea.getFechaLimite().isEqual(hoy)
                && tarea.getEstado() != EstadoTarea.COMPLETADA;
    }

    private boolean isOverdue(Tarea tarea, LocalDate hoy) {
        return tarea.getFechaLimite() != null
                && tarea.getFechaLimite().isBefore(hoy)
                && tarea.getEstado() != EstadoTarea.COMPLETADA;
    }

    private Comparator<Tarea> taskPriorityComparator(LocalDate hoy) {
        return Comparator
                .comparingInt((Tarea tarea) -> priorityBucket(tarea, hoy))
                .thenComparing(Tarea::getFechaLimite,
                        Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing((Tarea tarea) -> prioridadOrden(tarea.getPrioridad()))
                .thenComparing(Tarea::getCreadoEn, Comparator.reverseOrder());
    }

    private int priorityBucket(Tarea tarea, LocalDate hoy) {
        if (isOverdue(tarea, hoy)) {
            return 0;
        }
        if (tarea.getPrioridad() == PrioridadTarea.URGENTE) {
            return 1;
        }
        if (tarea.getEstado() == EstadoTarea.BLOQUEADA) {
            return 2;
        }
        if (isDueToday(tarea, hoy)) {
            return 3;
        }
        if (tarea.getFechaLimite() != null
                && tarea.getFechaLimite().isAfter(hoy)
                && !tarea.getFechaLimite().isAfter(hoy.plusDays(3))) {
            return 4;
        }
        if (tarea.getEstado() == EstadoTarea.EN_PROCESO
                || tarea.getEstado() == EstadoTarea.PENDIENTE) {
            return 5;
        }
        return 6;
    }

    private int prioridadOrden(PrioridadTarea prioridad) {
        return switch (prioridad) {
            case URGENTE -> 0;
            case ALTA -> 1;
            case MEDIA -> 2;
            case BAJA -> 3;
        };
    }

    private boolean actividadRelacionadaConTarea(
            ActividadResponse actividad,
            Tarea tarea) {
        return actividad.descripcion().contains("\"" + tarea.getTitulo() + "\"")
                || actividad.descripcion().contains("comento en \""
                        + tarea.getTitulo() + "\"");
    }

    private TareaAlertasResponse buildAlertas(Tarea tarea, LocalDate hoy) {
        boolean vencida = isOverdue(tarea, hoy);
        boolean venceHoy = isDueToday(tarea, hoy);
        boolean bloqueada = tarea.getEstado() == EstadoTarea.BLOQUEADA;
        boolean sinResponsable = tarea.getResponsable() == null;
        boolean requiereAtencion = vencida
                || venceHoy
                || bloqueada
                || tarea.getEstado() == EstadoTarea.EN_REVISION
                || sinResponsable;
        return new TareaAlertasResponse(
                vencida,
                venceHoy,
                bloqueada,
                sinResponsable,
                requiereAtencion);
    }

    private ComentarioResponse toComentarioResponse(
            Comentario comentario,
            Tarea tarea,
            String correo) {
        boolean puedeEliminar = comentario.getAutor().getCorreo()
                .equalsIgnoreCase(correo)
                || permisoService.puedeGestionar(tarea.getProyecto(), correo)
                || usuarioService.esAdmin(correo);
        return new ComentarioResponse(
                comentario.getId(),
                tarea.getId(),
                comentario.getAutor().getId(),
                comentario.getAutor().getNombre(),
                comentario.getContenido(),
                comentario.getCreadoEn(),
                puedeEliminar);
    }
}
