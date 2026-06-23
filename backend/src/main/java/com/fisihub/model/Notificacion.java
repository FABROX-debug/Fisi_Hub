package com.fisihub.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
        name = "notificacion",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_notificacion_usuario_tipo_referencia",
                columnNames = {"usuario_id", "tipo", "referencia_id"}))
public class Notificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TipoNotificacion tipo;

    @Column(nullable = false, length = 500)
    private String mensaje;

    @Column(name = "referencia_id", nullable = false)
    private Long referenciaId;

    @Column(nullable = false)
    private boolean leida;

    @Column(name = "creado_en", nullable = false, updatable = false)
    private LocalDateTime creadoEn;

    protected Notificacion() {
    }

    public Notificacion(
            Usuario usuario,
            TipoNotificacion tipo,
            String mensaje,
            Long referenciaId) {
        this.usuario = usuario;
        this.tipo = tipo;
        this.mensaje = mensaje;
        this.referenciaId = referenciaId;
    }

    @PrePersist
    void prePersist() {
        creadoEn = LocalDateTime.now();
    }

    public void marcarLeida() {
        leida = true;
    }

    public Long getId() {
        return id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public TipoNotificacion getTipo() {
        return tipo;
    }

    public String getMensaje() {
        return mensaje;
    }

    public Long getReferenciaId() {
        return referenciaId;
    }

    public boolean isLeida() {
        return leida;
    }

    public LocalDateTime getCreadoEn() {
        return creadoEn;
    }
}
