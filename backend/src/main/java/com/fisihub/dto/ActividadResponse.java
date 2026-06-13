package com.fisihub.dto;

import java.time.LocalDateTime;

import com.fisihub.model.TipoActividad;

public record ActividadResponse(
        Long id,
        TipoActividad tipo,
        String descripcion,
        Long usuarioId,
        String usuarioNombre,
        LocalDateTime fecha) {
}
