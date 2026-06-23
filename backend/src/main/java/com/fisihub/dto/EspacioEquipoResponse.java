package com.fisihub.dto;

import java.util.List;

public record EspacioEquipoResponse(
        Long espacioId,
        String espacioNombre,
        boolean puedeGestionar,
        List<EspacioMiembroResponse> miembros) {
}
