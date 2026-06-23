package com.fisihub.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fisihub.dto.NotificacionResponse;
import com.fisihub.service.NotificacionService;

@RestController
@RequestMapping("/api/notificaciones")
public class NotificacionController {

    private final NotificacionService notificacionService;

    public NotificacionController(NotificacionService notificacionService) {
        this.notificacionService = notificacionService;
    }

    @GetMapping
    public List<NotificacionResponse> listar(Authentication authentication) {
        return notificacionService.listar(authentication.getName());
    }

    @PatchMapping("/{id}/leida")
    public NotificacionResponse marcarLeida(
            @PathVariable Long id,
            Authentication authentication) {
        return notificacionService.marcarLeida(id, authentication.getName());
    }

    @PatchMapping("/leer-todas")
    public ResponseEntity<Void> marcarTodas(Authentication authentication) {
        notificacionService.marcarTodasLeidas(authentication.getName());
        return ResponseEntity.noContent().build();
    }
}
