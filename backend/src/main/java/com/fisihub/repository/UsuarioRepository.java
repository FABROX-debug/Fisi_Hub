package com.fisihub.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.fisihub.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    boolean existsByCorreoIgnoreCase(String correo);

    @EntityGraph(attributePaths = {"usuarioRoles", "usuarioRoles.rol"})
    Optional<Usuario> findByCorreoIgnoreCase(String correo);
}

