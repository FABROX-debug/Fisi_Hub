package com.fisihub.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.fisihub.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    boolean existsByCorreoIgnoreCase(String correo);

    @EntityGraph(attributePaths = {"usuarioRoles", "usuarioRoles.rol"})
    Optional<Usuario> findByCorreoIgnoreCase(String correo);

    /**
     * Retorna los usuarios activos que NO son miembros del espacio dado,
     * ordenados por nombre. Útil para el selector de invitación in-app.
     */
    @Query("""
            SELECT u FROM Usuario u
            WHERE u.activo = true
              AND u.id NOT IN (
                  SELECT em.usuario.id FROM EspacioMiembro em
                  WHERE em.espacio.id = :espacioId)
            ORDER BY u.nombre ASC
            """)
    List<Usuario> findActivosNoMiembrosDeEspacio(@Param("espacioId") Long espacioId);
}
