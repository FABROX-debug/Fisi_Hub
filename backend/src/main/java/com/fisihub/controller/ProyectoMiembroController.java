package com.fisihub.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fisihub.dto.MiembroProyectoRequest;
import com.fisihub.dto.MiembroProyectoResponse;
import com.fisihub.dto.ProyectoMiembrosResponse;
import com.fisihub.dto.RolProyectoRequest;
import com.fisihub.service.MiembroProyectoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/proyectos/{proyectoId}/miembros")
public class ProyectoMiembroController {

    private final MiembroProyectoService miembroService;

    public ProyectoMiembroController(MiembroProyectoService miembroService) {
        this.miembroService = miembroService;
    }

    @GetMapping
    public ProyectoMiembrosResponse listar(
            @PathVariable Long proyectoId,
            Authentication authentication) {
        return miembroService.listar(proyectoId, authentication.getName());
    }

    @PostMapping
    public ResponseEntity<MiembroProyectoResponse> agregar(
            @PathVariable Long proyectoId,
            @Valid @RequestBody MiembroProyectoRequest request,
            Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                miembroService.agregar(
                        proyectoId,
                        request,
                        authentication.getName()));
    }

    @PatchMapping("/{usuarioId}/rol")
    public MiembroProyectoResponse cambiarRol(
            @PathVariable Long proyectoId,
            @PathVariable Long usuarioId,
            @Valid @RequestBody RolProyectoRequest request,
            Authentication authentication) {
        return miembroService.cambiarRol(
                proyectoId,
                usuarioId,
                request,
                authentication.getName());
    }

    @DeleteMapping("/{usuarioId}")
    public ResponseEntity<Void> quitar(
            @PathVariable Long proyectoId,
            @PathVariable Long usuarioId,
            Authentication authentication) {
        miembroService.quitar(
                proyectoId,
                usuarioId,
                authentication.getName());
        return ResponseEntity.noContent().build();
    }
}
