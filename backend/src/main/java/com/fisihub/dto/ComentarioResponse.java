package com.fisihub.dto;

import java.time.LocalDateTime;

public record ComentarioResponse(
        Long id,
        Long tareaId,
        Long autorId,
        String autorNombre,
        String contenido,
        LocalDateTime creadoEn,
        boolean puedeEliminar) {
}
