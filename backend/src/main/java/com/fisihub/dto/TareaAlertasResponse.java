package com.fisihub.dto;

public record TareaAlertasResponse(
        boolean vencida,
        boolean venceHoy,
        boolean bloqueada,
        boolean sinResponsable,
        boolean requiereAtencion) {
}
