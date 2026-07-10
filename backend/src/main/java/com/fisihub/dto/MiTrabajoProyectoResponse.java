package com.fisihub.dto;

import java.time.LocalDate;

import com.fisihub.model.EstadoProyecto;
import com.fisihub.model.PrioridadProyecto;

public record MiTrabajoProyectoResponse(
        Long id,
        String nombre,
        String espacioNombre,
        EstadoProyecto estado,
        PrioridadProyecto prioridad,
        int porcentajeAvance,
        LocalDate fechaFin,
        String liderNombre,
        long tareasActivas) {
}
