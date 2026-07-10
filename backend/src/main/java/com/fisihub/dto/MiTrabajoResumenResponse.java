package com.fisihub.dto;

public record MiTrabajoResumenResponse(
        long pendientes,
        long enProceso,
        long enRevision,
        long bloqueadas,
        long completadas,
        long vencidas,
        long paraHoy) {
}
