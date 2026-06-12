package com.fisihub.dto;

import com.fisihub.model.EstadoTarea;

import jakarta.validation.constraints.NotNull;

public record EstadoTareaRequest(
        @NotNull(message = "El estado es obligatorio")
        EstadoTarea estado) {
}
