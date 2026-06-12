package com.fisihub.model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "espacio_trabajo")
public class EspacioTrabajo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String nombre;

    @Column(length = 500)
    private String descripcion;

    @Column(nullable = false, length = 7)
    private String color;

    @Column(nullable = false, length = 40)
    private String icono;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "creado_por_id", nullable = false)
    private Usuario creadoPor;

    @Column(name = "creado_en", nullable = false, updatable = false)
    private LocalDateTime creadoEn;

    @OneToMany(mappedBy = "espacio", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<EspacioMiembro> miembros = new HashSet<>();

    @OneToMany(mappedBy = "espacio", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Proyecto> proyectos = new HashSet<>();

    protected EspacioTrabajo() {
    }

    public EspacioTrabajo(
            String nombre,
            String descripcion,
            String color,
            String icono,
            Usuario creadoPor) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.color = color;
        this.icono = icono;
        this.creadoPor = creadoPor;
    }

    @PrePersist
    void prePersist() {
        creadoEn = LocalDateTime.now();
    }

    public void actualizar(
            String nombre,
            String descripcion,
            String color,
            String icono) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.color = color;
        this.icono = icono;
    }

    public void agregarMiembro(Usuario usuario, RolEspacio rol) {
        miembros.add(new EspacioMiembro(this, usuario, rol));
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

    public String getColor() {
        return color;
    }

    public String getIcono() {
        return icono;
    }

    public Usuario getCreadoPor() {
        return creadoPor;
    }

    public LocalDateTime getCreadoEn() {
        return creadoEn;
    }

    public Set<EspacioMiembro> getMiembros() {
        return miembros;
    }

    public Set<Proyecto> getProyectos() {
        return proyectos;
    }
}
