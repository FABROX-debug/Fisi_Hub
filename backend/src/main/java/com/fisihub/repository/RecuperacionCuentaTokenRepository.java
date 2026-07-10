package com.fisihub.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fisihub.model.RecuperacionCuentaToken;
import com.fisihub.model.Usuario;

public interface RecuperacionCuentaTokenRepository
        extends JpaRepository<RecuperacionCuentaToken, Long> {

    Optional<RecuperacionCuentaToken> findByTokenHash(String tokenHash);

    Optional<RecuperacionCuentaToken> findByUsuario(Usuario usuario);
}
