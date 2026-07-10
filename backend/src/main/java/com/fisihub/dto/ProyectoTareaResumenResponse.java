package com.fisihub.dto;

public record ProyectoTareaResumenResponse(
        long total,
        long pendientes,
        long enProceso,
        long enRevision,
        long bloqueadas,
        long completadas,
        long vencidas) {
}
