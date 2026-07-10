package com.fisihub.dto;

import java.util.List;

public record TareaDetalleResponse(
        TareaResponse tarea,
        TareaDetalleProyectoResponse proyecto,
        List<ComentarioResponse> comentarios,
        List<ActividadResponse> actividad,
        TareaAlertasResponse alertas) {
}
