package com.fisihub.dto;

public record ReporteMiembroResponse(
        Long usuarioId,
        String nombre,
        long tareasAsignadas,
        long tareasCompletadas) {
}
