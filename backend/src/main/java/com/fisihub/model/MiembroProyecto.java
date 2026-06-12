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
        name = "miembro_proyecto",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_miembro_proyecto",
                columnNames = {"proyecto_id", "usuario_id"}))
public class MiembroProyecto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "proyecto_id", nullable = false)
    private Proyecto proyecto;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    @Column(name = "rol_en_proyecto", nullable = false, length = 20)
    private RolProyecto rol;

    protected MiembroProyecto() {
    }

    public MiembroProyecto(
            Proyecto proyecto,
            Usuario usuario,
            RolProyecto rol) {
        this.proyecto = proyecto;
        this.usuario = usuario;
        this.rol = rol;
    }

    public Long getId() {
        return id;
    }

    public Proyecto getProyecto() {
        return proyecto;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public RolProyecto getRol() {
        return rol;
    }
}
