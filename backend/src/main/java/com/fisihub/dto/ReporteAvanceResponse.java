package com.fisihub.dto;

import java.util.List;

public record ReporteAvanceResponse(
        Long proyectoId,
        String proyectoNombre,
        int porcentajeAvance,
        long totalTareas,
        long tareasCompletadas,
        long tareasPendientes,
        long tareasEnProceso,
        long tareasEnRevision,
        long tareasBloqueadas,
        long tareasVencidas,
        List<ReporteMiembroResponse> productividadMiembros) {
}
