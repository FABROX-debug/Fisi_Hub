package com.fisihub.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.fisihub.model.EspacioTrabajo;

public interface EspacioTrabajoRepository
        extends JpaRepository<EspacioTrabajo, Long> {

    @EntityGraph(attributePaths = {"creadoPor", "miembros", "proyectos"})
    List<EspacioTrabajo> findDistinctByMiembrosUsuarioCorreoIgnoreCaseOrderByCreadoEnDesc(
            String correo);

    @EntityGraph(attributePaths = {"creadoPor", "miembros", "proyectos"})
    Optional<EspacioTrabajo> findDistinctByIdAndMiembrosUsuarioCorreoIgnoreCase(
            Long id,
            String correo);
}
