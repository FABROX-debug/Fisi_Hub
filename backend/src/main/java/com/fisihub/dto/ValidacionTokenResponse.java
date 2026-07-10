package com.fisihub.dto;

public record ValidacionTokenResponse(
        boolean valid,
        String correo,
        String message) {
}
