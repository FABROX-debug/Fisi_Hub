package com.fisihub.dto;

import com.fisihub.model.RolProyecto;

public record MiembroProyectoRequest(
        String correo,
        Long usuarioId,
        RolProyecto rol) {
}
