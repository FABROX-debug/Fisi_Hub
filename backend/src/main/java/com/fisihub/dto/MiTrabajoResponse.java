package com.fisihub.dto;

import java.util.List;

public record MiTrabajoResponse(
        MiTrabajoResumenResponse resumen,
        List<TareaResponse> tareasPrioritarias,
        List<TareaResponse> tareasAsignadas,
        List<TareaResponse> tareasNecesitanAccion,
        List<MiTrabajoProyectoResponse> proyectosConCarga) {
}
