package com.fisihub.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fisihub.dto.AdminEstadisticasResponse;
import com.fisihub.dto.AdminProyectoResponse;
import com.fisihub.dto.AdminUsuarioResponse;
import com.fisihub.exception.BusinessRuleException;
import com.fisihub.exception.ResourceNotFoundException;
import com.fisihub.model.EstadoTarea;
import com.fisihub.model.Usuario;
import com.fisihub.repository.ProyectoRepository;
import com.fisihub.repository.TareaRepository;
import com.fisihub.repository.UsuarioRepository;

@Service
public class AdminService {

    private final UsuarioRepository usuarioRepository;
    private final ProyectoRepository proyectoRepository;
    private final TareaRepository tareaRepository;
    private final UsuarioService usuarioService;

    public AdminService(
            UsuarioRepository usuarioRepository,
            ProyectoRepository proyectoRepository,
            TareaRepository tareaRepository,
            UsuarioService usuarioService) {
        this.usuarioRepository = usuarioRepository;
        this.proyectoRepository = proyectoRepository;
        this.tareaRepository = tareaRepository;
        this.usuarioService = usuarioService;
    }

    @Transactional(readOnly = true)
    public List<AdminUsuarioResponse> listarUsuarios() {
        return usuarioRepository.findAll().stream()
                .map(this::toUsuarioResponse)
                .toList();
    }

    @Transactional
    public AdminUsuarioResponse cambiarEstado(
            Long usuarioId,
            boolean activo,
            String correoAdmin) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Usuario no encontrado"));
        if (!activo && usuario.getCorreo().equalsIgnoreCase(correoAdmin)) {
            throw new BusinessRuleException(
                    "Un administrador no puede desactivar su propia cuenta");
        }
        if (activo) {
            usuario.activar();
        } else {
            usuario.desactivar();
        }
        return toUsuarioResponse(usuario);
    }

    @Transactional(readOnly = true)
    public List<AdminProyectoResponse> listarProyectos() {
        return proyectoRepository.findAllByOrderByCreadoEnDesc().stream()
                .map(proyecto -> new AdminProyectoResponse(
                        proyecto.getId(),
                        proyecto.getNombre(),
                        proyecto.getEspacio().getNombre(),
                        proyecto.getLider().getNombre(),
                        proyecto.getEstado(),
                        proyecto.getPorcentajeAvance(),
                        proyecto.getMiembros().size()))
                .toList();
    }

    @Transactional(readOnly = true)
    public AdminEstadisticasResponse estadisticas() {
        long totalTareas = tareaRepository.count();
        long completadas = tareaRepository.countByEstado(
                EstadoTarea.COMPLETADA);
        int tasa = totalTareas == 0
                ? 0
                : (int) Math.round(completadas * 100.0 / totalTareas);
        return new AdminEstadisticasResponse(
                usuarioRepository.count(),
                usuarioRepository.findAll().stream()
                        .filter(Usuario::isActivo)
                        .count(),
                proyectoRepository.count(),
                totalTareas,
                completadas,
                tasa);
    }

    private AdminUsuarioResponse toUsuarioResponse(Usuario usuario) {
        var response = usuarioService.toResponse(usuario);
        return new AdminUsuarioResponse(
                response.id(),
                response.nombre(),
                response.correo(),
                response.activo(),
                response.roles(),
                response.creadoEn());
    }
}
