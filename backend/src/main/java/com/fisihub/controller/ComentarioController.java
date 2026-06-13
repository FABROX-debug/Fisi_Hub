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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fisihub.dto.ComentarioRequest;
import com.fisihub.dto.ComentarioResponse;
import com.fisihub.service.ComentarioService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class ComentarioController {

    private final ComentarioService comentarioService;

    public ComentarioController(ComentarioService comentarioService) {
        this.comentarioService = comentarioService;
    }

    @GetMapping("/tareas/{tareaId}/comentarios")
    public List<ComentarioResponse> listar(
            @PathVariable Long tareaId,
            Authentication authentication) {
        return comentarioService.listar(tareaId, authentication.getName());
    }

    @PostMapping("/tareas/{tareaId}/comentarios")
    public ResponseEntity<ComentarioResponse> crear(
            @PathVariable Long tareaId,
            @Valid @RequestBody ComentarioRequest request,
            Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                comentarioService.crear(
                        tareaId,
                        request,
                        authentication.getName()));
    }

    @DeleteMapping("/comentarios/{id}")
    public ResponseEntity<Void> eliminar(
            @PathVariable Long id,
            Authentication authentication) {
        comentarioService.eliminar(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}
