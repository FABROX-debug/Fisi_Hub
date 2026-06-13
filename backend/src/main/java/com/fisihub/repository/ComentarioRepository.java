package com.fisihub.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.fisihub.model.Comentario;

public interface ComentarioRepository extends JpaRepository<Comentario, Long> {

    @EntityGraph(attributePaths = {"autor", "tarea", "tarea.proyecto"})
    List<Comentario> findByTareaIdOrderByCreadoEnAsc(Long tareaId);

    @EntityGraph(attributePaths = {"autor", "tarea", "tarea.proyecto"})
    Optional<Comentario> findById(Long id);
}
