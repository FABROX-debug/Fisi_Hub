package com.fisihub.dto;

import com.fisihub.model.RolProyecto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record MiembroProyectoRequest(
        @NotBlank(message = "El correo es obligatorio")
        @Email(message = "El correo no tiene un formato valido")
        String correo,
        RolProyecto rol) {
}
