package com.fisihub.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record EspacioRequest(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 120, message = "El nombre no puede superar 120 caracteres")
        String nombre,

        @Size(max = 500, message = "La descripcion no puede superar 500 caracteres")
        String descripcion,

        @Pattern(
                regexp = "^#[0-9A-Fa-f]{6}$",
                message = "El color debe usar el formato hexadecimal #RRGGBB")
        String color,

        @Size(max = 40, message = "El icono no puede superar 40 caracteres")
        String icono) {
}
