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
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "proyecto")
public class Proyecto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 140)
    private String nombre;

    @Column(length = 1000)
    private String descripcion;

    @Column(name = "fecha_inicio")
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin")
    private LocalDate fechaFin;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoProyecto estado;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PrioridadProyecto prioridad;

    @Column(name = "porcentaje_avance", nullable = false)
    private int porcentajeAvance;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "espacio_id", nullable = false)
    private EspacioTrabajo espacio;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "lider_id", nullable = false)
    private Usuario lider;

    @Column(name = "creado_en", nullable = false, updatable = false)
    private LocalDateTime creadoEn;

    @OneToMany(mappedBy = "proyecto", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<MiembroProyecto> miembros = new HashSet<>();

    @OneToMany(mappedBy = "proyecto", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Tarea> tareas = new HashSet<>();

    protected Proyecto() {
    }

    public Proyecto(
            String nombre,
            String descripcion,
            LocalDate fechaInicio,
            LocalDate fechaFin,
            EstadoProyecto estado,
            PrioridadProyecto prioridad,
            EspacioTrabajo espacio,
            Usuario lider) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.estado = estado;
        this.prioridad = prioridad;
        this.espacio = espacio;
        this.lider = lider;
        this.porcentajeAvance = 0;
    }

    @PrePersist
    void prePersist() {
        creadoEn = LocalDateTime.now();
    }

    public void actualizar(
            String nombre,
            String descripcion,
            LocalDate fechaInicio,
            LocalDate fechaFin,
            EstadoProyecto estado,
            PrioridadProyecto prioridad) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.estado = estado;
        this.prioridad = prioridad;
    }

    public void agregarMiembro(Usuario usuario, RolProyecto rol) {
        miembros.add(new MiembroProyecto(this, usuario, rol));
    }

    public void actualizarPorcentajeAvance(int porcentajeAvance) {
        this.porcentajeAvance = Math.min(100, Math.max(0, porcentajeAvance));
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public EstadoProyecto getEstado() {
        return estado;
    }

    public PrioridadProyecto getPrioridad() {
        return prioridad;
    }

    public int getPorcentajeAvance() {
        return porcentajeAvance;
    }

    public EspacioTrabajo getEspacio() {
        return espacio;
    }

    public Usuario getLider() {
        return lider;
    }

    public LocalDateTime getCreadoEn() {
        return creadoEn;
    }

    public Set<MiembroProyecto> getMiembros() {
        return miembros;
    }

    public Set<Tarea> getTareas() {
        return tareas;
    }
}
