package com.fisihub.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fisihub.dto.AuthResponse;
import com.fisihub.dto.LoginRequest;
import com.fisihub.dto.RecuperacionCuentaResponse;
import com.fisihub.dto.RegisterRequest;
import com.fisihub.dto.ResetPasswordRequest;
import com.fisihub.dto.SolicitarRecuperacionRequest;
import com.fisihub.dto.UsuarioResponse;
import com.fisihub.dto.ValidacionTokenResponse;
import com.fisihub.service.AuthService;
import com.fisihub.service.RecuperacionCuentaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final RecuperacionCuentaService recuperacionCuentaService;

    public AuthController(
            AuthService authService,
            RecuperacionCuentaService recuperacionCuentaService) {
        this.authService = authService;
        this.recuperacionCuentaService = recuperacionCuentaService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<RecuperacionCuentaResponse> forgotPassword(
            @Valid @RequestBody SolicitarRecuperacionRequest request) {
        return ResponseEntity.ok(recuperacionCuentaService.solicitar(request));
    }

    @GetMapping("/reset-password/{token}")
    public ResponseEntity<ValidacionTokenResponse> validateResetPasswordToken(
            @PathVariable String token) {
        return ResponseEntity.ok(recuperacionCuentaService.validar(token));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<RecuperacionCuentaResponse> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {
        return ResponseEntity.ok(recuperacionCuentaService.restablecer(request));
    }

    @GetMapping("/me")
    public ResponseEntity<UsuarioResponse> me(Authentication authentication) {
        return ResponseEntity.ok(authService.me(authentication.getName()));
    }
}
