package com.fisihub.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "comentario")
public class Comentario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tarea_id", nullable = false)
    private Tarea tarea;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "autor_id", nullable = false)
    private Usuario autor;

    @Column(nullable = false, length = 2000)
    private String contenido;

    @Column(name = "creado_en", nullable = false, updatable = false)
    private LocalDateTime creadoEn;

    protected Comentario() {
    }

    public Comentario(Tarea tarea, Usuario autor, String contenido) {
        this.tarea = tarea;
        this.autor = autor;
        this.contenido = contenido;
    }

    @PrePersist
    void prePersist() {
        creadoEn = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Tarea getTarea() {
        return tarea;
    }

    public Usuario getAutor() {
        return autor;
    }

    public String getContenido() {
        return contenido;
    }

    public LocalDateTime getCreadoEn() {
        return creadoEn;
    }
}
