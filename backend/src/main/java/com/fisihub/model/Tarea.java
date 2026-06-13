package com.fisihub.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;

@Entity
@Table(name = "tarea")
public class Tarea {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 180)
    private String titulo;

    @Column(length = 2000)
    private String descripcion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "proyecto_id", nullable = false)
    private Proyecto proyecto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responsable_id")
    private Usuario responsable;

    @Column(name = "fecha_limite")
    private LocalDate fechaLimite;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoTarea estado;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PrioridadTarea prioridad;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "creado_por_id", nullable = false)
    private Usuario creadoPor;

    @Column(name = "creado_en", nullable = false, updatable = false)
    private LocalDateTime creadoEn;

    @Column(name = "actualizado_en", nullable = false)
    private LocalDateTime actualizadoEn;

    @OneToMany(mappedBy = "tarea", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Comentario> comentarios = new HashSet<>();

    protected Tarea() {
    }

    public Tarea(
            String titulo,
            String descripcion,
            Proyecto proyecto,
            Usuario responsable,
            LocalDate fechaLimite,
            EstadoTarea estado,
            PrioridadTarea prioridad,
            Usuario creadoPor) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.proyecto = proyecto;
        this.responsable = responsable;
        this.fechaLimite = fechaLimite;
        this.estado = estado;
        this.prioridad = prioridad;
        this.creadoPor = creadoPor;
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

    public void actualizar(
            String titulo,
            String descripcion,
            Usuario responsable,
            LocalDate fechaLimite,
            EstadoTarea estado,
            PrioridadTarea prioridad) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.responsable = responsable;
        this.fechaLimite = fechaLimite;
        this.estado = estado;
        this.prioridad = prioridad;
    }

    public void cambiarEstado(EstadoTarea estado) {
        this.estado = estado;
    }

    public Long getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public Proyecto getProyecto() {
        return proyecto;
    }

    public Usuario getResponsable() {
        return responsable;
    }

    public LocalDate getFechaLimite() {
        return fechaLimite;
    }

    public EstadoTarea getEstado() {
        return estado;
    }

    public PrioridadTarea getPrioridad() {
        return prioridad;
    }

    public Usuario getCreadoPor() {
        return creadoPor;
    }

    public LocalDateTime getCreadoEn() {
        return creadoEn;
    }

    public LocalDateTime getActualizadoEn() {
        return actualizadoEn;
    }
}
