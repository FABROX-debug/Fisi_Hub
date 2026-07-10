package com.fisihub.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "recuperacion_cuenta_token")
public class RecuperacionCuentaToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false, unique = true)
    private Usuario usuario;

    @Column(name = "token_hash", nullable = false, unique = true, length = 64)
    private String tokenHash;

    @Column(name = "expira_en", nullable = false)
    private LocalDateTime expiraEn;

    @Column(name = "usado_en")
    private LocalDateTime usadoEn;

    @Column(name = "creado_en", nullable = false, updatable = false)
    private LocalDateTime creadoEn;

    @Column(name = "actualizado_en", nullable = false)
    private LocalDateTime actualizadoEn;

    protected RecuperacionCuentaToken() {
    }

    public RecuperacionCuentaToken(
            Usuario usuario,
            String tokenHash,
            LocalDateTime expiraEn) {
        this.usuario = usuario;
        this.tokenHash = tokenHash;
        this.expiraEn = expiraEn;
    }

    @PrePersist
    void prePersist() {
        creadoEn = LocalDateTime.now();
        actualizadoEn = creadoEn;
    }

    @PreUpdate
    void preUpdate() {
        actualizadoEn = LocalDateTime.now();
    }

    public boolean estaExpirado(LocalDateTime ahora) {
        return expiraEn.isBefore(ahora) || expiraEn.isEqual(ahora);
    }

    public boolean fueUsado() {
        return usadoEn != null;
    }

    public void marcarUsado(LocalDateTime fecha) {
        usadoEn = fecha;
    }

    public void reemplazar(String nuevoTokenHash, LocalDateTime nuevaExpiracion) {
        tokenHash = nuevoTokenHash;
        expiraEn = nuevaExpiracion;
        usadoEn = null;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public String getTokenHash() {
        return tokenHash;
    }
}
