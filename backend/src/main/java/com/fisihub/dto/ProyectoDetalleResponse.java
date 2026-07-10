package com.fisihub.dto;

import java.util.List;

public record ProyectoDetalleResponse(
        ProyectoResponse proyecto,
        ProyectoTareaResumenResponse resumenTareas,
        List<TareaResponse> tareasDestacadas,
        ProyectoMiembrosResponse miembros,
        List<ActividadResponse> actividadReciente) {
}
