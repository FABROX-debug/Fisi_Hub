package com.fisihub.dto;

import com.fisihub.model.RolProyecto;

import jakarta.validation.constraints.NotNull;

public record RolProyectoRequest(
        @NotNull(message = "El rol es obligatorio")
        RolProyecto rol) {
}
