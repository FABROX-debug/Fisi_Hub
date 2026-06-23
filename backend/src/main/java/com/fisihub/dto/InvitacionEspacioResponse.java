package com.fisihub.dto;

import java.time.LocalDateTime;

import com.fisihub.model.EstadoInvitacion;
import com.fisihub.model.RolEspacio;

public record InvitacionEspacioResponse(
        Long id,
        Long usuarioId,
        String usuarioNombre,
        Long espacioId,
        String espacioNombre,
        RolEspacio rol,
        EstadoInvitacion estado,
        LocalDateTime expiraEn,
        Long invitadoPorId,
        String invitadoPorNombre,
        LocalDateTime creadoEn) {
}
