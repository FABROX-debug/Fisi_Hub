package com.fisihub.model;

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
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
        name = "espacio_miembro",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_espacio_miembro",
                columnNames = {"espacio_id", "usuario_id"}))
public class EspacioMiembro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "espacio_id", nullable = false)
    private EspacioTrabajo espacio;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RolEspacio rol;

    protected EspacioMiembro() {
    }

    public EspacioMiembro(
            EspacioTrabajo espacio,
            Usuario usuario,
            RolEspacio rol) {
        this.espacio = espacio;
        this.usuario = usuario;
        this.rol = rol;
    }

    public Long getId() {
        return id;
    }

    public EspacioTrabajo getEspacio() {
        return espacio;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public RolEspacio getRol() {
        return rol;
    }
}
