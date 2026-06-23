package com.fisihub.dto;

import com.fisihub.model.RolProyecto;

public record MiembroProyectoResponse(
        Long usuarioId,
        String nombre,
        String correo,
        RolProyecto rol,
        long tareasActivas,
        boolean liderDesignado,
        boolean activo) {
}
