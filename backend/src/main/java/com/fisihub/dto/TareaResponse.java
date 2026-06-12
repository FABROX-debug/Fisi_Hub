package com.fisihub.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fisihub.model.EstadoTarea;
import com.fisihub.model.PrioridadTarea;

public record TareaResponse(
        Long id,
        String titulo,
        String descripcion,
        Long proyectoId,
        String proyectoNombre,
        Long responsableId,
        String responsableNombre,
        LocalDate fechaLimite,
        EstadoTarea estado,
        PrioridadTarea prioridad,
        Long creadoPorId,
        String creadoPorNombre,
        LocalDateTime creadoEn,
        LocalDateTime actualizadoEn) {
}
