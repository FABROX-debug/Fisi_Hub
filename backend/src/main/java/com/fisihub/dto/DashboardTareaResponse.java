package com.fisihub.dto;

import java.time.LocalDate;

import com.fisihub.model.EstadoTarea;
import com.fisihub.model.PrioridadTarea;

public record DashboardTareaResponse(
        Long id,
        String titulo,
        Long proyectoId,
        String proyectoNombre,
        LocalDate fechaLimite,
        EstadoTarea estado,
        PrioridadTarea prioridad,
        String responsableNombre) {
}
