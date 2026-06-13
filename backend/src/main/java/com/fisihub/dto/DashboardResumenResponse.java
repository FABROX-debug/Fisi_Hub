package com.fisihub.dto;

import java.util.List;

public record DashboardResumenResponse(
        long totalProyectosActivos,
        long tareasPendientes,
        long tareasCompletadas,
        long tareasVencidas,
        long tareasParaHoy,
        int porcentajePromedioAvance,
        List<DashboardProyectoResponse> proyectosActivosRecientes,
        List<DashboardTareaResponse> tareasProximas,
        List<DashboardTareaResponse> tareasVencidasDetalle,
        List<DashboardActividadResponse> actividadReciente) {
}
