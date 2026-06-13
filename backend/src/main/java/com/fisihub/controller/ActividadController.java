package com.fisihub.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fisihub.dto.ActividadResponse;
import com.fisihub.service.HistorialActividadService;

@RestController
@RequestMapping("/api/proyectos/{proyectoId}/actividad")
public class ActividadController {

    private final HistorialActividadService historialService;

    public ActividadController(
            HistorialActividadService historialService) {
        this.historialService = historialService;
    }

    @GetMapping
    public List<ActividadResponse> listar(
            @PathVariable Long proyectoId,
            Authentication authentication) {
        return historialService.listarPorProyecto(
                proyectoId,
                authentication.getName());
    }
}
