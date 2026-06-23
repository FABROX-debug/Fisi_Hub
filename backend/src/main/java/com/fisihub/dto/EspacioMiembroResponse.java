package com.fisihub.dto;

import com.fisihub.model.RolEspacio;

public record EspacioMiembroResponse(
        Long usuarioId,
        String nombre,
        String correo,
        RolEspacio rol,
        boolean activo,
        long tareasActivas) {
}
