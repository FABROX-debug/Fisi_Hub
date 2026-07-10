package com.fisihub.repository;

import java.util.List;
import java.util.Optional;
import java.time.LocalDate;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.fisihub.model.EstadoTarea;
import com.fisihub.model.Tarea;

public interface TareaRepository extends JpaRepository<Tarea, Long> {

    @EntityGraph(attributePaths = {"proyecto", "responsable", "creadoPor"})
    List<Tarea> findDistinctByProyectoMiembrosUsuarioCorreoIgnoreCaseOrderByCreadoEnDesc(
            String correo);

    @EntityGraph(attributePaths = {"proyecto", "responsable", "creadoPor"})
    List<Tarea> findDistinctByProyectoIdAndProyectoMiembrosUsuarioCorreoIgnoreCaseOrderByCreadoEnDesc(
            Long proyectoId,
            String correo);

    @EntityGraph(attributePaths = {"proyecto", "responsable", "creadoPor"})
    Optional<Tarea> findDistinctByIdAndProyectoMiembrosUsuarioCorreoIgnoreCase(
            Long id,
            String correo);

    long countByProyectoId(Long proyectoId);

    long countByProyectoIdAndEstado(Long proyectoId, EstadoTarea estado);

    long countByProyectoIdAndResponsableId(
            Long proyectoId,
            Long responsableId);

    long countByProyectoIdAndResponsableIdAndEstadoNot(
            Long proyectoId,
            Long responsableId,
            EstadoTarea estado);

    long countByProyectoEspacioIdAndResponsableIdAndEstadoNot(
            Long espacioId,
            Long responsableId,
            EstadoTarea estado);

    @EntityGraph(attributePaths = {"proyecto", "responsable", "creadoPor"})
    List<Tarea> findByProyectoIdOrderByCreadoEnDesc(Long proyectoId);

    @EntityGraph(attributePaths = {"proyecto", "responsable", "creadoPor"})
    List<Tarea> findAllByOrderByCreadoEnDesc();

    @EntityGraph(attributePaths = {"proyecto", "responsable", "creadoPor"})
    List<Tarea> findByResponsableCorreoIgnoreCaseOrderByCreadoEnDesc(
            String correo);

    @EntityGraph(attributePaths = {"proyecto", "responsable"})
    List<Tarea> findByResponsableIdAndFechaLimiteAndEstadoNot(
            Long responsableId,
            LocalDate fechaLimite,
            EstadoTarea estado);

    long countByEstado(EstadoTarea estado);
}
