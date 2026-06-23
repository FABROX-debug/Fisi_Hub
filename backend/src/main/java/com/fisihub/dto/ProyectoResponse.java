package com.fisihub.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fisihub.model.EstadoProyecto;
import com.fisihub.model.PrioridadProyecto;

public record ProyectoResponse(
        Long id,
        String nombre,
        String descripcion,
        LocalDate fechaInicio,
        LocalDate fechaFin,
        EstadoProyecto estado,
        PrioridadProyecto prioridad,
        int porcentajeAvance,
        Long espacioId,
        String espacioNombre,
        Long liderId,
        String liderNombre,
        int cantidadMiembros,
        LocalDateTime creadoEn,
        boolean puedeGestionar) {
}
