package com.fisihub.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.fisihub.model.EspacioMiembro;
import com.fisihub.model.RolEspacio;

public interface EspacioMiembroRepository
        extends JpaRepository<EspacioMiembro, Long> {

    boolean existsByEspacioIdAndUsuarioCorreoIgnoreCase(
            Long espacioId,
            String correo);

    Optional<EspacioMiembro> findByEspacioIdAndUsuarioCorreoIgnoreCase(
            Long espacioId,
            String correo);

    @EntityGraph(attributePaths = {"usuario", "usuario.usuarioRoles", "usuario.usuarioRoles.rol"})
    List<EspacioMiembro> findByEspacioIdOrderByUsuarioNombreAsc(Long espacioId);

    @EntityGraph(attributePaths = {"usuario"})
    Optional<EspacioMiembro> findByEspacioIdAndUsuarioId(
            Long espacioId,
            Long usuarioId);

    boolean existsByEspacioIdAndUsuarioId(Long espacioId, Long usuarioId);

    long countByEspacioIdAndRol(Long espacioId, RolEspacio rol);
}
