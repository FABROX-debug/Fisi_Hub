package com.fisihub.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fisihub.dto.ReporteAvanceResponse;
import com.fisihub.dto.ReporteMiembroResponse;
import com.fisihub.model.EstadoTarea;
import com.fisihub.model.Proyecto;
import com.fisihub.model.Tarea;
import com.fisihub.repository.MiembroProyectoRepository;
import com.fisihub.repository.TareaRepository;

@Service
public class ReporteService {

    private final ProyectoService proyectoService;
    private final TareaRepository tareaRepository;
    private final MiembroProyectoRepository miembroRepository;

    public ReporteService(
            ProyectoService proyectoService,
            TareaRepository tareaRepository,
            MiembroProyectoRepository miembroRepository) {
        this.proyectoService = proyectoService;
        this.tareaRepository = tareaRepository;
        this.miembroRepository = miembroRepository;
    }

    @Transactional(readOnly = true)
    public ReporteAvanceResponse obtenerAvance(
            Long proyectoId,
            String correo) {
        Proyecto proyecto = proyectoService.buscarAccesible(proyectoId, correo);
        List<Tarea> tareas =
                tareaRepository.findByProyectoIdOrderByCreadoEnDesc(proyectoId);
        LocalDate hoy = LocalDate.now();

        long completadas = contarEstado(tareas, EstadoTarea.COMPLETADA);
        List<ReporteMiembroResponse> productividad = miembroRepository
                .findByProyectoIdOrderByUsuarioNombreAsc(proyectoId)
                .stream()
                .map(miembro -> {
                    long asignadas = tareas.stream()
                            .filter(tarea -> tarea.getResponsable() != null
                                    && tarea.getResponsable().getId().equals(
                                            miembro.getUsuario().getId()))
                            .count();
                    long terminadas = tareas.stream()
                            .filter(tarea -> tarea.getResponsable() != null
                                    && tarea.getResponsable().getId().equals(
                                            miembro.getUsuario().getId())
                                    && tarea.getEstado()
                                            == EstadoTarea.COMPLETADA)
                            .count();
                    return new ReporteMiembroResponse(
                            miembro.getUsuario().getId(),
                            miembro.getUsuario().getNombre(),
                            asignadas,
                            terminadas);
                })
                .toList();

        return new ReporteAvanceResponse(
                proyecto.getId(),
                proyecto.getNombre(),
                proyecto.getPorcentajeAvance(),
                tareas.size(),
                completadas,
                contarEstado(tareas, EstadoTarea.PENDIENTE),
                contarEstado(tareas, EstadoTarea.EN_PROCESO),
                contarEstado(tareas, EstadoTarea.EN_REVISION),
                contarEstado(tareas, EstadoTarea.BLOQUEADA),
                tareas.stream()
                        .filter(tarea -> tarea.getFechaLimite() != null
                                && tarea.getFechaLimite().isBefore(hoy)
                                && tarea.getEstado() != EstadoTarea.COMPLETADA)
                        .count(),
                productividad);
    }

    private long contarEstado(List<Tarea> tareas, EstadoTarea estado) {
        return tareas.stream()
                .filter(tarea -> tarea.getEstado() == estado)
                .count();
    }
}
