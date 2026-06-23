package com.fisihub.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fisihub.dto.ReporteAvanceResponse;
import com.fisihub.service.ReporteService;

@RestController
@RequestMapping("/api/proyectos/{proyectoId}/reportes")
public class ReporteController {

    private final ReporteService reporteService;

    public ReporteController(ReporteService reporteService) {
        this.reporteService = reporteService;
    }

    @GetMapping("/avance")
    public ReporteAvanceResponse avance(
            @PathVariable Long proyectoId,
            Authentication authentication) {
        return reporteService.obtenerAvance(
                proyectoId,
                authentication.getName());
    }
}
