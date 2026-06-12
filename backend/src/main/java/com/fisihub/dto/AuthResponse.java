package com.fisihub.dto;

public record AuthResponse(
        String token,
        String tokenType,
        long expiresIn,
        UsuarioResponse usuario) {

    @Override
    public String toString() {
        return "AuthResponse[token=[PROTECTED], tokenType=" + tokenType
                + ", expiresIn=" + expiresIn
                + ", usuario=" + usuario + "]";
    }
}
