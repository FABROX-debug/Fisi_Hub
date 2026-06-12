package com.fisihub.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fisihub.model.Rol;
import com.fisihub.model.RolNombre;

public interface RolRepository extends JpaRepository<Rol, Long> {

    Optional<Rol> findByNombre(RolNombre nombre);
}

