package com.fisihub.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fisihub.dto.TareaResponse;
import com.fisihub.service.TareaService;

@RestController
@RequestMapping("/api/proyectos")
public class ProyectoTareaController {

    private final TareaService tareaService;

    public ProyectoTareaController(TareaService tareaService) {
        this.tareaService = tareaService;
    }

    @GetMapping("/{id}/tareas")
    public List<TareaResponse> listarTareas(
            @PathVariable Long id,
            Authentication authentication) {
        return tareaService.listarPorProyecto(id, authentication.getName());
    }
}
