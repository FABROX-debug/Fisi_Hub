package com.fisihub.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.fisihub.model.HistorialActividad;

public interface HistorialActividadRepository
        extends JpaRepository<HistorialActividad, Long> {

    @EntityGraph(attributePaths = {"proyecto", "usuario"})
    List<HistorialActividad> findByProyectoIdOrderByFechaDesc(Long proyectoId);

    @EntityGraph(attributePaths = {"proyecto", "usuario"})
    List<HistorialActividad>
            findDistinctByProyectoMiembrosUsuarioCorreoIgnoreCaseOrderByFechaDesc(
                    String correo);
}
