package com.fisihub.dto;

import com.fisihub.model.RolEspacio;

import jakarta.validation.constraints.NotNull;

public record RolEspacioRequest(
        @NotNull(message = "El rol es obligatorio")
        RolEspacio rol) {
}
