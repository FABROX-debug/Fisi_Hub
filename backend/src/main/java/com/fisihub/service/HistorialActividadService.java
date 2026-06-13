package com.fisihub.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fisihub.dto.ActividadResponse;
import com.fisihub.model.HistorialActividad;
import com.fisihub.model.Proyecto;
import com.fisihub.model.TipoActividad;
import com.fisihub.model.Usuario;
import com.fisihub.repository.HistorialActividadRepository;
import com.fisihub.repository.ProyectoRepository;

@Service
public class HistorialActividadService {

    private static final int MAX_ACTIVIDADES_PROYECTO = 30;
    private static final int MAX_ACTIVIDADES_DASHBOARD = 5;

    private final HistorialActividadRepository historialRepository;
    private final ProyectoRepository proyectoRepository;

    public HistorialActividadService(
            HistorialActividadRepository historialRepository,
            ProyectoRepository proyectoRepository) {
        this.historialRepository = historialRepository;
        this.proyectoRepository = proyectoRepository;
    }

    @Transactional
    public void registrar(
            Proyecto proyecto,
            Usuario usuario,
            TipoActividad tipo,
            String descripcion) {
        historialRepository.save(new HistorialActividad(
                proyecto,
                usuario,
                tipo,
                descripcion));
    }

    @Transactional(readOnly = true)
    public List<ActividadResponse> listarPorProyecto(
            Long proyectoId,
            String correo) {
        validarAcceso(proyectoId, correo);
        return historialRepository
                .findByProyectoIdOrderByFechaDesc(proyectoId)
                .stream()
                .limit(MAX_ACTIVIDADES_PROYECTO)
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ActividadResponse> listarRecientesDelUsuario(String correo) {
        return historialRepository
                .findDistinctByProyectoMiembrosUsuarioCorreoIgnoreCaseOrderByFechaDesc(
                        correo)
                .stream()
                .limit(MAX_ACTIVIDADES_DASHBOARD)
                .map(this::toResponse)
                .toList();
    }

    private void validarAcceso(Long proyectoId, String correo) {
        proyectoRepository
                .findDistinctByIdAndMiembrosUsuarioCorreoIgnoreCase(
                        proyectoId,
                        correo)
                .orElseThrow(() -> new com.fisihub.exception.ResourceNotFoundException(
                        "Proyecto no encontrado"));
    }

    private ActividadResponse toResponse(HistorialActividad actividad) {
        return new ActividadResponse(
                actividad.getId(),
                actividad.getTipo(),
                actividad.getDescripcion(),
                actividad.getUsuario().getId(),
                actividad.getUsuario().getNombre(),
                actividad.getFecha());
    }
}
