package com.fisihub.dto;

import java.time.LocalDateTime;
import java.util.Set;

public record AdminUsuarioResponse(
        Long id,
        String nombre,
        String correo,
        boolean activo,
        Set<String> roles,
        LocalDateTime creadoEn) {
}
