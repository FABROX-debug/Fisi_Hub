package com.fisihub.dto;

import com.fisihub.model.RolEspacio;

import jakarta.validation.constraints.NotNull;

public record InvitacionEspacioRequest(
        @NotNull(message = "El id de usuario es obligatorio")
        Long usuarioId,
        RolEspacio rol) {
}
