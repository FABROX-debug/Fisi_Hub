package com.fisihub.dto;

import java.time.LocalDate;

import com.fisihub.model.EstadoTarea;
import com.fisihub.model.PrioridadTarea;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record TareaCreateRequest(
        @NotBlank(message = "El titulo es obligatorio")
        @Size(max = 180, message = "El titulo no puede superar 180 caracteres")
        String titulo,

        @Size(max = 2000, message = "La descripcion no puede superar 2000 caracteres")
        String descripcion,

        @NotNull(message = "El proyecto es obligatorio")
        Long proyectoId,

        Long responsableId,

        @FutureOrPresent(message = "La fecha limite no puede estar en el pasado")
        LocalDate fechaLimite,

        EstadoTarea estado,
        PrioridadTarea prioridad) {
}
