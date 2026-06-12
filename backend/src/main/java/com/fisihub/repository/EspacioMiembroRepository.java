package com.fisihub.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fisihub.model.EspacioMiembro;

public interface EspacioMiembroRepository
        extends JpaRepository<EspacioMiembro, Long> {

    boolean existsByEspacioIdAndUsuarioCorreoIgnoreCase(
            Long espacioId,
            String correo);

    Optional<EspacioMiembro> findByEspacioIdAndUsuarioCorreoIgnoreCase(
            Long espacioId,
            String correo);
}
