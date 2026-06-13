package com.fisihub.dto;

import java.time.LocalDateTime;

public record DashboardActividadResponse(
        String descripcion,
        LocalDateTime fecha) {
}
