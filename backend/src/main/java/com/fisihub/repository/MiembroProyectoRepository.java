package com.fisihub.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fisihub.model.MiembroProyecto;

public interface MiembroProyectoRepository
        extends JpaRepository<MiembroProyecto, Long> {

    Optional<MiembroProyecto> findByProyectoIdAndUsuarioCorreoIgnoreCase(
            Long proyectoId,
            String correo);
}
