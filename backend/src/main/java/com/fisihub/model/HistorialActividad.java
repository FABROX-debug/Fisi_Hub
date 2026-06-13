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

@Entity
@Table(name = "historial_actividad")
public class HistorialActividad {

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
    @Column(nullable = false, length = 40)
    private TipoActividad tipo;

    @Column(nullable = false, length = 500)
    private String descripcion;

    @Column(nullable = false, updatable = false)
    private LocalDateTime fecha;

    protected HistorialActividad() {
    }

    public HistorialActividad(
            Proyecto proyecto,
            Usuario usuario,
            TipoActividad tipo,
            String descripcion) {
        this.proyecto = proyecto;
        this.usuario = usuario;
        this.tipo = tipo;
        this.descripcion = descripcion;
    }

    @PrePersist
    void prePersist() {
        fecha = LocalDateTime.now();
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

    public TipoActividad getTipo() {
        return tipo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }
}
