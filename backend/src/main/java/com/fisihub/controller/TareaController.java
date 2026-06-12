package com.fisihub.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fisihub.dto.EstadoTareaRequest;
import com.fisihub.dto.TareaCreateRequest;
import com.fisihub.dto.TareaResponse;
import com.fisihub.dto.TareaUpdateRequest;
import com.fisihub.model.EstadoTarea;
import com.fisihub.model.PrioridadTarea;
import com.fisihub.service.TareaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/tareas")
public class TareaController {

    private final TareaService tareaService;

    public TareaController(TareaService tareaService) {
        this.tareaService = tareaService;
    }

    @GetMapping
    public List<TareaResponse> listar(
            @RequestParam(required = false) EstadoTarea estado,
            @RequestParam(required = false) PrioridadTarea prioridad,
            @RequestParam(required = false) Long proyectoId,
            @RequestParam(required = false) Long responsableId,
            Authentication authentication) {
        return tareaService.listar(
                authentication.getName(),
                estado,
                prioridad,
                proyectoId,
                responsableId);
    }

    @PostMapping
    public ResponseEntity<TareaResponse> crear(
            @Valid @RequestBody TareaCreateRequest request,
            Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(tareaService.crear(request, authentication.getName()));
    }

    @GetMapping("/{id}")
    public TareaResponse obtener(
            @PathVariable Long id,
            Authentication authentication) {
        return tareaService.obtener(id, authentication.getName());
    }

    @PutMapping("/{id}")
    public TareaResponse actualizar(
            @PathVariable Long id,
            @Valid @RequestBody TareaUpdateRequest request,
            Authentication authentication) {
        return tareaService.actualizar(
                id,
                request,
                authentication.getName());
    }

    @PatchMapping("/{id}/estado")
    public TareaResponse cambiarEstado(
            @PathVariable Long id,
            @Valid @RequestBody EstadoTareaRequest request,
            Authentication authentication) {
        return tareaService.cambiarEstado(
                id,
                request,
                authentication.getName());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @PathVariable Long id,
            Authentication authentication) {
        tareaService.eliminar(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}
