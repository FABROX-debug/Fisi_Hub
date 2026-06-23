package com.fisihub.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fisihub.dto.EspacioRequest;
import com.fisihub.dto.EspacioResponse;
import com.fisihub.dto.EspacioEquipoResponse;
import com.fisihub.dto.EspacioMiembroResponse;
import com.fisihub.dto.ProyectoResponse;
import com.fisihub.dto.RolEspacioRequest;
import com.fisihub.service.EspacioMiembroService;
import com.fisihub.service.EspacioService;
import com.fisihub.service.ProyectoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/espacios")
public class EspacioController {

    private final EspacioService espacioService;
    private final EspacioMiembroService miembroService;
    private final ProyectoService proyectoService;

    public EspacioController(
            EspacioService espacioService,
            EspacioMiembroService miembroService,
            ProyectoService proyectoService) {
        this.espacioService = espacioService;
        this.miembroService = miembroService;
        this.proyectoService = proyectoService;
    }

    @GetMapping
    public List<EspacioResponse> listar(Authentication authentication) {
        return espacioService.listar(authentication.getName());
    }

    @PostMapping
    public ResponseEntity<EspacioResponse> crear(
            @Valid @RequestBody EspacioRequest request,
            Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(espacioService.crear(request, authentication.getName()));
    }

    @GetMapping("/{id}")
    public EspacioResponse obtener(
            @PathVariable Long id,
            Authentication authentication) {
        return espacioService.obtener(id, authentication.getName());
    }

    @PutMapping("/{id}")
    public EspacioResponse actualizar(
            @PathVariable Long id,
            @Valid @RequestBody EspacioRequest request,
            Authentication authentication) {
        return espacioService.actualizar(
                id,
                request,
                authentication.getName());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @PathVariable Long id,
            Authentication authentication) {
        espacioService.eliminar(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/proyectos")
    public List<ProyectoResponse> listarProyectos(
            @PathVariable Long id,
            Authentication authentication) {
        return proyectoService.listarPorEspacio(
                id,
                authentication.getName());
    }

    @GetMapping("/{id}/miembros")
    public EspacioEquipoResponse listarMiembros(
            @PathVariable Long id,
            Authentication authentication) {
        return miembroService.listar(id, authentication.getName());
    }

    @PatchMapping("/{id}/miembros/{usuarioId}/rol")
    public EspacioMiembroResponse cambiarRolMiembro(
            @PathVariable Long id,
            @PathVariable Long usuarioId,
            @Valid @RequestBody RolEspacioRequest request,
            Authentication authentication) {
        return miembroService.cambiarRol(
                id,
                usuarioId,
                request,
                authentication.getName());
    }

    @DeleteMapping("/{id}/miembros/{usuarioId}")
    public ResponseEntity<Void> quitarMiembro(
            @PathVariable Long id,
            @PathVariable Long usuarioId,
            Authentication authentication) {
        miembroService.quitar(id, usuarioId, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}
