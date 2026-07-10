package com.fisihub.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fisihub.dto.ComentarioRequest;
import com.fisihub.dto.ComentarioResponse;
import com.fisihub.exception.ForbiddenOperationException;
import com.fisihub.exception.ResourceNotFoundException;
import com.fisihub.model.Comentario;
import com.fisihub.model.Proyecto;
import com.fisihub.model.RolProyecto;
import com.fisihub.model.Tarea;
import com.fisihub.model.TipoActividad;
import com.fisihub.model.Usuario;
import com.fisihub.repository.ComentarioRepository;
import com.fisihub.repository.MiembroProyectoRepository;

@Service
public class ComentarioService {

    private final ComentarioRepository comentarioRepository;
    private final MiembroProyectoRepository miembroProyectoRepository;
    private final TareaService tareaService;
    private final UsuarioService usuarioService;
    private final HistorialActividadService historialService;

    public ComentarioService(
            ComentarioRepository comentarioRepository,
            MiembroProyectoRepository miembroProyectoRepository,
            TareaService tareaService,
            UsuarioService usuarioService,
            HistorialActividadService historialService) {
        this.comentarioRepository = comentarioRepository;
        this.miembroProyectoRepository = miembroProyectoRepository;
        this.tareaService = tareaService;
        this.usuarioService = usuarioService;
        this.historialService = historialService;
    }

    @Transactional(readOnly = true)
    public List<ComentarioResponse> listar(Long tareaId, String correo) {
        Tarea tarea = tareaService.buscarAccesible(tareaId, correo);
        return listarPorTareaAccesible(tarea, correo);
    }

    @Transactional
    public ComentarioResponse crear(
            Long tareaId,
            ComentarioRequest request,
            String correo) {
        Tarea tarea = tareaService.buscarAccesible(tareaId, correo);
        Usuario autor = usuarioService.buscarPorCorreo(correo);
        Comentario comentario = comentarioRepository.save(new Comentario(
                tarea,
                autor,
                request.contenido().trim()));
        historialService.registrar(
                tarea.getProyecto(),
                autor,
                TipoActividad.COMENTARIO_CREADO,
                autor.getNombre() + " comento en \"" + tarea.getTitulo() + "\"");
        return toResponse(comentario, tarea, correo);
    }

    @Transactional
    public void eliminar(Long comentarioId, String correo) {
        Comentario comentario = comentarioRepository.findById(comentarioId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Comentario no encontrado"));
        Proyecto proyecto = comentario.getTarea().getProyecto();
        boolean esAdmin = usuarioService.esAdmin(correo);
        if (!esAdmin) {
            tareaService.buscarAccesible(comentario.getTarea().getId(), correo);
        }
        boolean esAutor = comentario.getAutor().getCorreo()
                .equalsIgnoreCase(correo);
        boolean esLider = esLider(proyecto, correo);
        if (!esAutor && !esLider && !esAdmin) {
            throw new ForbiddenOperationException(
                    "No tienes permiso para eliminar este comentario");
        }
        comentarioRepository.delete(comentario);
    }

    private ComentarioResponse toResponse(
            Comentario comentario,
            Tarea tarea,
            String correo) {
        boolean puedeEliminar = comentario.getAutor().getCorreo()
                .equalsIgnoreCase(correo)
                || esLider(tarea.getProyecto(), correo)
                || usuarioService.esAdmin(correo);
        return new ComentarioResponse(
                comentario.getId(),
                tarea.getId(),
                comentario.getAutor().getId(),
                comentario.getAutor().getNombre(),
                comentario.getContenido(),
                comentario.getCreadoEn(),
                puedeEliminar);
    }

    @Transactional(readOnly = true)
    public List<ComentarioResponse> listarPorTareaAccesible(
            Tarea tarea,
            String correo) {
        return comentarioRepository.findByTareaIdOrderByCreadoEnAsc(tarea.getId())
                .stream()
                .map(comentario -> toResponse(comentario, tarea, correo))
                .toList();
    }

    private boolean esLider(Proyecto proyecto, String correo) {
        return proyecto.getLider().getCorreo().equalsIgnoreCase(correo)
                || miembroProyectoRepository
                        .findByProyectoIdAndUsuarioCorreoIgnoreCase(
                                proyecto.getId(),
                                correo)
                        .map(miembro -> miembro.getRol() == RolProyecto.LIDER)
                        .orElse(false);
    }
}
