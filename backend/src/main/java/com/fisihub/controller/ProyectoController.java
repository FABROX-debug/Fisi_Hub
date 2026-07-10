package com.fisihub.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fisihub.dto.ProyectoRequest;
import com.fisihub.dto.ProyectoDetalleResponse;
import com.fisihub.dto.ProyectoResponse;
import com.fisihub.service.ProyectoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/proyectos")
public class ProyectoController {

    private final ProyectoService proyectoService;

    public ProyectoController(ProyectoService proyectoService) {
        this.proyectoService = proyectoService;
    }

    @GetMapping
    public List<ProyectoResponse> listar(Authentication authentication) {
        return proyectoService.listar(authentication.getName());
    }

    @PostMapping
    public ResponseEntity<ProyectoResponse> crear(
            @Valid @RequestBody ProyectoRequest request,
            Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(proyectoService.crear(request, authentication.getName()));
    }

    @GetMapping("/{id}")
    public ProyectoResponse obtener(
            @PathVariable Long id,
            Authentication authentication) {
        return proyectoService.obtener(id, authentication.getName());
    }

    @GetMapping("/{id}/detalle")
    public ProyectoDetalleResponse obtenerDetalle(
            @PathVariable Long id,
            Authentication authentication) {
        return proyectoService.obtenerDetalle(id, authentication.getName());
    }

    @PutMapping("/{id}")
    public ProyectoResponse actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ProyectoRequest request,
            Authentication authentication) {
        return proyectoService.actualizar(
                id,
                request,
                authentication.getName());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @PathVariable Long id,
            Authentication authentication) {
        proyectoService.eliminar(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}
