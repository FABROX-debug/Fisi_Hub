package com.fisihub.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.fisihub.model.MiembroProyecto;
import com.fisihub.model.RolProyecto;

public interface MiembroProyectoRepository
        extends JpaRepository<MiembroProyecto, Long> {

    Optional<MiembroProyecto> findByProyectoIdAndUsuarioCorreoIgnoreCase(
            Long proyectoId,
            String correo);

    @EntityGraph(attributePaths = {"usuario"})
    Optional<MiembroProyecto> findByProyectoIdAndUsuarioId(
            Long proyectoId,
            Long usuarioId);

    @EntityGraph(attributePaths = {"usuario", "usuario.usuarioRoles", "usuario.usuarioRoles.rol"})
    List<MiembroProyecto> findByProyectoIdOrderByUsuarioNombreAsc(Long proyectoId);

    boolean existsByProyectoIdAndUsuarioId(Long proyectoId, Long usuarioId);

    long countByProyectoIdAndRol(Long proyectoId, RolProyecto rol);
}
