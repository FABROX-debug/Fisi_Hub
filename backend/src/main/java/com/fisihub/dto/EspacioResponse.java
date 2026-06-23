package com.fisihub.dto;

import java.time.LocalDateTime;

public record EspacioResponse(
        Long id,
        String nombre,
        String descripcion,
        String color,
        String icono,
        Long creadorId,
        String creadorNombre,
        int cantidadMiembros,
        int cantidadProyectos,
        LocalDateTime creadoEn,
        boolean puedeGestionar) {
}
