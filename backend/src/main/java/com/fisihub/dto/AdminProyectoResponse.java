package com.fisihub.dto;

import com.fisihub.model.EstadoProyecto;

public record AdminProyectoResponse(
        Long id,
        String nombre,
        String espacioNombre,
        String liderNombre,
        EstadoProyecto estado,
        int porcentajeAvance,
        int totalMiembros) {
}
