package com.fisihub.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fisihub.dto.EstadoTareaRequest;
import com.fisihub.dto.TareaCreateRequest;
import com.fisihub.dto.TareaResponse;
import com.fisihub.dto.TareaUpdateRequest;
import com.fisihub.exception.BusinessRuleException;
import com.fisihub.exception.ResourceNotFoundException;
import com.fisihub.model.EstadoTarea;
import com.fisihub.model.MiembroProyecto;
import com.fisihub.model.PrioridadTarea;
import com.fisihub.model.Proyecto;
import com.fisihub.model.Tarea;
import com.fisihub.model.Usuario;
import com.fisihub.repository.MiembroProyectoRepository;
import com.fisihub.repository.TareaRepository;

@Service
public class TareaService {

    private final TareaRepository tareaRepository;
    private final MiembroProyectoRepository miembroProyectoRepository;
    private final ProyectoService proyectoService;
    private final UsuarioService usuarioService;

    public TareaService(
            TareaRepository tareaRepository,
            MiembroProyectoRepository miembroProyectoRepository,
            ProyectoService proyectoService,
            UsuarioService usuarioService) {
        this.tareaRepository = tareaRepository;
        this.miembroProyectoRepository = miembroProyectoRepository;
        this.proyectoService = proyectoService;
        this.usuarioService = usuarioService;
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
        return toResponse(guardada);
    }

    @Transactional(readOnly = true)
    public List<TareaResponse> listar(
            String correo,
            EstadoTarea estado,
            PrioridadTarea prioridad,
            Long proyectoId,
            Long responsableId) {
        List<Tarea> tareas = proyectoId == null
                ? tareaRepository
                        .findDistinctByProyectoMiembrosUsuarioCorreoIgnoreCaseOrderByCreadoEnDesc(
                                correo)
                : tareaRepository
                        .findDistinctByProyectoIdAndProyectoMiembrosUsuarioCorreoIgnoreCaseOrderByCreadoEnDesc(
                                proyectoId,
                                correo);

        return tareas.stream()
                .filter(tarea -> estado == null || tarea.getEstado() == estado)
                .filter(tarea -> prioridad == null
                        || tarea.getPrioridad() == prioridad)
                .filter(tarea -> responsableId == null
                        || tarea.getResponsable() != null
                        && tarea.getResponsable().getId().equals(responsableId))
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TareaResponse> listarPorProyecto(
            Long proyectoId,
            String correo) {
        proyectoService.buscarAccesible(proyectoId, correo);
        return tareaRepository
                .findDistinctByProyectoIdAndProyectoMiembrosUsuarioCorreoIgnoreCaseOrderByCreadoEnDesc(
                        proyectoId,
                        correo)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public TareaResponse obtener(Long id, String correo) {
        return toResponse(buscarAccesible(id, correo));
    }

    @Transactional
    public TareaResponse actualizar(
            Long id,
            TareaUpdateRequest request,
            String correo) {
        Tarea tarea = buscarAccesible(id, correo);
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
        return toResponse(tarea);
    }

    @Transactional
    public TareaResponse cambiarEstado(
            Long id,
            EstadoTareaRequest request,
            String correo) {
        Tarea tarea = buscarAccesible(id, correo);
        tarea.cambiarEstado(request.estado());
        tareaRepository.flush();
        recalcularAvance(tarea.getProyecto());
        return toResponse(tarea);
    }

    @Transactional
    public void eliminar(Long id, String correo) {
        Tarea tarea = buscarAccesible(id, correo);
        Proyecto proyecto = tarea.getProyecto();
        tareaRepository.delete(tarea);
        tareaRepository.flush();
        recalcularAvance(proyecto);
    }

    @Transactional(readOnly = true)
    public Tarea buscarAccesible(Long id, String correo) {
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

    private TareaResponse toResponse(Tarea tarea) {
        Usuario responsable = tarea.getResponsable();
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
                tarea.getActualizadoEn());
    }

    private String normalizarOpcional(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
