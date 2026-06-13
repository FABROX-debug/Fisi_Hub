package com.fisihub.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ComentarioRequest(
        @NotBlank(message = "El comentario no puede estar vacio")
        @Size(max = 2000, message = "El comentario no puede superar 2000 caracteres")
        String contenido) {
}
