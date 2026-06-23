package com.fisihub.dto;

public record AdminEstadisticasResponse(
        long totalUsuarios,
        long usuariosActivos,
        long totalProyectos,
        long totalTareas,
        long tareasCompletadas,
        int tasaCompletitud) {
}
