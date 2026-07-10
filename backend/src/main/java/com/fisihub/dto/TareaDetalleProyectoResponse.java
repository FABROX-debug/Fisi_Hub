package com.fisihub.dto;

import com.fisihub.model.EstadoProyecto;

public record TareaDetalleProyectoResponse(
        Long id,
        String nombre,
        String espacioNombre,
        EstadoProyecto estado,
        int porcentajeAvance) {
}
