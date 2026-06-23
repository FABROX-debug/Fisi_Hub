package com.fisihub.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fisihub.dto.AdminEstadisticasResponse;
import com.fisihub.dto.AdminProyectoResponse;
import com.fisihub.dto.AdminUsuarioResponse;
import com.fisihub.service.AdminService;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/usuarios")
    public List<AdminUsuarioResponse> usuarios() {
        return adminService.listarUsuarios();
    }

    @PatchMapping("/usuarios/{id}/activar")
    public AdminUsuarioResponse activar(
            @PathVariable Long id,
            Authentication authentication) {
        return adminService.cambiarEstado(
                id,
                true,
                authentication.getName());
    }

    @PatchMapping("/usuarios/{id}/desactivar")
    public AdminUsuarioResponse desactivar(
            @PathVariable Long id,
            Authentication authentication) {
        return adminService.cambiarEstado(
                id,
                false,
                authentication.getName());
    }

    @GetMapping("/proyectos")
    public List<AdminProyectoResponse> proyectos() {
        return adminService.listarProyectos();
    }

    @GetMapping("/estadisticas")
    public AdminEstadisticasResponse estadisticas() {
        return adminService.estadisticas();
    }
}
