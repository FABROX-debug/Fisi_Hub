package com.fisihub.dto;

import java.util.List;

public record ProyectoMiembrosResponse(
        Long proyectoId,
        String proyectoNombre,
        boolean puedeGestionar,
        List<MiembroProyectoResponse> miembros) {
}
