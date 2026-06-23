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
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "invitacion_espacio")
public class InvitacionEspacio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "espacio_id", nullable = false)
    private EspacioTrabajo espacio;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RolEspacio rol;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoInvitacion estado;

    @Column(name = "expira_en", nullable = false)
    private LocalDateTime expiraEn;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "invitado_por_id", nullable = false)
    private Usuario invitadoPor;

    @Column(name = "creado_en", nullable = false, updatable = false)
    private LocalDateTime creadoEn;

    @Column(name = "actualizado_en", nullable = false)
    private LocalDateTime actualizadoEn;

    protected InvitacionEspacio() {
    }

    public InvitacionEspacio(
            Usuario usuario,
            EspacioTrabajo espacio,
            RolEspacio rol,
            LocalDateTime expiraEn,
            Usuario invitadoPor) {
        this.usuario = usuario;
        this.espacio = espacio;
        this.rol = rol;
        this.expiraEn = expiraEn;
        this.invitadoPor = invitadoPor;
        this.estado = EstadoInvitacion.PENDIENTE;
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

    public void expirarSiCorresponde(LocalDateTime ahora) {
        if (estado == EstadoInvitacion.PENDIENTE && !expiraEn.isAfter(ahora)) {
            estado = EstadoInvitacion.EXPIRADA;
        }
    }

    public void aceptar() {
        estado = EstadoInvitacion.ACEPTADA;
    }

    public void revocar() {
        estado = EstadoInvitacion.REVOCADA;
    }

    public Long getId() {
        return id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public EspacioTrabajo getEspacio() {
        return espacio;
    }

    public RolEspacio getRol() {
        return rol;
    }

    public EstadoInvitacion getEstado() {
        return estado;
    }

    public LocalDateTime getExpiraEn() {
        return expiraEn;
    }

    public Usuario getInvitadoPor() {
        return invitadoPor;
    }

    public LocalDateTime getCreadoEn() {
        return creadoEn;
    }

    public LocalDateTime getActualizadoEn() {
        return actualizadoEn;
    }
}
