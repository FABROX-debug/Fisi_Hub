package com.fisihub.service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fisihub.dto.DashboardProyectoResponse;
import com.fisihub.dto.DashboardResumenResponse;
import com.fisihub.dto.DashboardTareaResponse;
import com.fisihub.dto.DashboardActividadResponse;
import com.fisihub.model.EstadoProyecto;
import com.fisihub.model.EstadoTarea;
import com.fisihub.model.Proyecto;
import com.fisihub.model.Tarea;
import com.fisihub.model.Usuario;
import com.fisihub.repository.ProyectoRepository;
import com.fisihub.repository.TareaRepository;

@Service
public class DashboardService {

    private static final int MAX_PROYECTOS = 5;
    private static final int MAX_TAREAS = 8;

    private final ProyectoRepository proyectoRepository;
    private final TareaRepository tareaRepository;
    private final HistorialActividadService historialService;

    public DashboardService(
            ProyectoRepository proyectoRepository,
            TareaRepository tareaRepository,
            HistorialActividadService historialService) {
        this.proyectoRepository = proyectoRepository;
        this.tareaRepository = tareaRepository;
        this.historialService = historialService;
    }

    @Transactional(readOnly = true)
    public DashboardResumenResponse obtenerResumen(String correo) {
        LocalDate hoy = LocalDate.now();
        LocalDate limiteProximas = hoy.plusDays(3);
        List<Proyecto> proyectos = proyectoRepository
                .findDistinctByMiembrosUsuarioCorreoIgnoreCaseOrderByCreadoEnDesc(
                        correo);
        List<Tarea> tareas = tareaRepository
                .findDistinctByProyectoMiembrosUsuarioCorreoIgnoreCaseOrderByCreadoEnDesc(
                        correo);

        List<Proyecto> proyectosActivos = proyectos.stream()
                .filter(this::esProyectoActivo)
                .toList();
        List<Tarea> tareasVencidas = tareas.stream()
                .filter(tarea -> tarea.getEstado() != EstadoTarea.COMPLETADA)
                .filter(tarea -> tarea.getFechaLimite() != null)
                .filter(tarea -> tarea.getFechaLimite().isBefore(hoy))
                .sorted(Comparator.comparing(Tarea::getFechaLimite))
                .toList();
        List<DashboardTareaResponse> tareasProximas = tareas.stream()
                .filter(tarea -> tarea.getEstado() != EstadoTarea.COMPLETADA)
                .filter(tarea -> tarea.getFechaLimite() != null)
                .filter(tarea -> !tarea.getFechaLimite().isBefore(hoy))
                .filter(tarea -> !tarea.getFechaLimite()
                        .isAfter(limiteProximas))
                .sorted(Comparator.comparing(Tarea::getFechaLimite))
                .limit(MAX_TAREAS)
                .map(this::toTareaResponse)
                .toList();

        long tareasCompletadas = tareas.stream()
                .filter(tarea -> tarea.getEstado() == EstadoTarea.COMPLETADA)
                .count();
        long tareasPendientes = tareas.size() - tareasCompletadas;
        long tareasParaHoy = tareas.stream()
                .filter(tarea -> hoy.equals(tarea.getFechaLimite()))
                .count();
        int promedioAvance = (int) Math.round(proyectos.stream()
                .mapToInt(Proyecto::getPorcentajeAvance)
                .average()
                .orElse(0));

        return new DashboardResumenResponse(
                proyectosActivos.size(),
                tareasPendientes,
                tareasCompletadas,
                tareasVencidas.size(),
                tareasParaHoy,
                promedioAvance,
                proyectosActivos.stream()
                        .limit(MAX_PROYECTOS)
                        .map(this::toProyectoResponse)
                        .toList(),
                tareasProximas,
                tareasVencidas.stream()
                        .limit(MAX_TAREAS)
                        .map(this::toTareaResponse)
                        .toList(),
                historialService.listarRecientesDelUsuario(correo)
                        .stream()
                        .map(actividad -> new DashboardActividadResponse(
                                actividad.tipo().name(),
                                actividad.descripcion(),
                                actividad.usuarioNombre(),
                                actividad.fecha()))
                        .toList());
    }

    private boolean esProyectoActivo(Proyecto proyecto) {
        return proyecto.getEstado() != EstadoProyecto.FINALIZADO
                && proyecto.getEstado() != EstadoProyecto.CANCELADO;
    }

    private DashboardProyectoResponse toProyectoResponse(Proyecto proyecto) {
        return new DashboardProyectoResponse(
                proyecto.getId(),
                proyecto.getNombre(),
                proyecto.getEstado(),
                proyecto.getPrioridad(),
                proyecto.getPorcentajeAvance(),
                proyecto.getFechaFin(),
                proyecto.getEspacio().getNombre(),
                proyecto.getLider().getNombre());
    }

    private DashboardTareaResponse toTareaResponse(Tarea tarea) {
        Usuario responsable = tarea.getResponsable();
        return new DashboardTareaResponse(
                tarea.getId(),
                tarea.getTitulo(),
                tarea.getProyecto().getId(),
                tarea.getProyecto().getNombre(),
                tarea.getFechaLimite(),
                tarea.getEstado(),
                tarea.getPrioridad(),
                responsable == null ? null : responsable.getNombre());
    }
}
