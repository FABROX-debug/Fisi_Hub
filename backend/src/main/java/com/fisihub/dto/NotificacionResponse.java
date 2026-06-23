package com.fisihub.dto;

import java.time.LocalDateTime;

import com.fisihub.model.EstadoInvitacion;
import com.fisihub.model.TipoNotificacion;

public record NotificacionResponse(
        Long id,
        TipoNotificacion tipo,
        String mensaje,
        Long referenciaId,
        EstadoInvitacion invitacionEstado,
        boolean leida,
        LocalDateTime creadoEn) {
}
