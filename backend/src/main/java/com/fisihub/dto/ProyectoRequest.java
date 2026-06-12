package com.fisihub.dto;

import java.time.LocalDate;

import com.fisihub.model.EstadoProyecto;
import com.fisihub.model.PrioridadProyecto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ProyectoRequest(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 140, message = "El nombre no puede superar 140 caracteres")
        String nombre,

        @Size(max = 1000, message = "La descripcion no puede superar 1000 caracteres")
        String descripcion,

        @NotNull(message = "El espacio es obligatorio")
        Long espacioId,

        LocalDate fechaInicio,
        LocalDate fechaFin,
        EstadoProyecto estado,
        PrioridadProyecto prioridad) {
}
