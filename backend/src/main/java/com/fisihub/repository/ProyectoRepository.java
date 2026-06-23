package com.fisihub.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.fisihub.model.Proyecto;

public interface ProyectoRepository extends JpaRepository<Proyecto, Long> {

    @EntityGraph(attributePaths = {"espacio", "lider", "miembros"})
    List<Proyecto> findDistinctByMiembrosUsuarioCorreoIgnoreCaseOrderByCreadoEnDesc(
            String correo);

    @EntityGraph(attributePaths = {"espacio", "lider", "miembros"})
    List<Proyecto> findDistinctByEspacioIdAndMiembrosUsuarioCorreoIgnoreCaseOrderByCreadoEnDesc(
            Long espacioId,
            String correo);

    @EntityGraph(attributePaths = {"espacio", "lider", "miembros"})
    Optional<Proyecto> findDistinctByIdAndMiembrosUsuarioCorreoIgnoreCase(
            Long id,
            String correo);

    @EntityGraph(attributePaths = {"espacio", "lider", "miembros"})
    List<Proyecto> findAllByOrderByCreadoEnDesc();
}
