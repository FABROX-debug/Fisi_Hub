package com.fisihub.dto;

import java.time.LocalDate;

import com.fisihub.model.EstadoProyecto;
import com.fisihub.model.PrioridadProyecto;

public record DashboardProyectoResponse(
        Long id,
        String nombre,
        EstadoProyecto estado,
        PrioridadProyecto prioridad,
        int porcentajeAvance,
        LocalDate fechaFin,
        String espacioNombre,
        String liderNombre) {
}
