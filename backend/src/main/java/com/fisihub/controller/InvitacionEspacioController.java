package com.fisihub.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fisihub.dto.InvitacionEspacioRequest;
import com.fisihub.dto.InvitacionEspacioResponse;
import com.fisihub.dto.UsuarioDisponibleResponse;
import com.fisihub.service.InvitacionEspacioService;

import jakarta.validation.Valid;

@RestController
public class InvitacionEspacioController {

    private final InvitacionEspacioService invitacionService;

    public InvitacionEspacioController(InvitacionEspacioService invitacionService) {
        this.invitacionService = invitacionService;
    }

    /** Crea una invitación in-app para un usuario del sistema. */
    @PostMapping("/api/espacios/{espacioId}/invitaciones")
    public ResponseEntity<InvitacionEspacioResponse> crear(
            @PathVariable Long espacioId,
            @Valid @RequestBody InvitacionEspacioRequest request,
            Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                invitacionService.crear(espacioId, request, authentication.getName()));
    }

    /** Lista todas las invitaciones de un espacio (solo para líderes/admins). */
    @GetMapping("/api/espacios/{espacioId}/invitaciones")
    public List<InvitacionEspacioResponse> listar(
            @PathVariable Long espacioId,
            Authentication authentication) {
        return invitacionService.listar(espacioId, authentication.getName());
    }

    /** Lista los usuarios activos que aún no son miembros del espacio. */
    @GetMapping("/api/espacios/{espacioId}/usuarios-disponibles")
    public List<UsuarioDisponibleResponse> listarDisponibles(
            @PathVariable Long espacioId,
            Authentication authentication) {
        return invitacionService.listarDisponibles(espacioId, authentication.getName());
    }

    /** Acepta una invitación pendiente (solo el usuario destinatario). */
    @PostMapping("/api/invitaciones/{id}/aceptar")
    public InvitacionEspacioResponse aceptar(
            @PathVariable Long id,
            Authentication authentication) {
        return invitacionService.aceptar(id, authentication.getName());
    }

    /** Rechaza una invitación pendiente (solo el usuario destinatario). */
    @PostMapping("/api/invitaciones/{id}/rechazar")
    public InvitacionEspacioResponse rechazar(
            @PathVariable Long id,
            Authentication authentication) {
        return invitacionService.rechazar(id, authentication.getName());
    }

    /** Reenvía una invitación (solo líderes/admins del espacio). */
    @PostMapping("/api/invitaciones/{id}/reenviar")
    public InvitacionEspacioResponse reenviar(
            @PathVariable Long id,
            Authentication authentication) {
        return invitacionService.reenviar(id, authentication.getName());
    }

    /** Revoca una invitación (solo líderes/admins del espacio). */
    @DeleteMapping("/api/invitaciones/{id}")
    public ResponseEntity<Void> revocar(
            @PathVariable Long id,
            Authentication authentication) {
        invitacionService.revocar(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}
