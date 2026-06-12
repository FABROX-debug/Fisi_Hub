package com.fisihub.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fisihub.dto.EspacioRequest;
import com.fisihub.dto.EspacioResponse;
import com.fisihub.exception.ForbiddenOperationException;
import com.fisihub.exception.ResourceNotFoundException;
import com.fisihub.model.EspacioTrabajo;
import com.fisihub.model.RolEspacio;
import com.fisihub.model.Usuario;
import com.fisihub.repository.EspacioTrabajoRepository;

@Service
public class EspacioService {

    private static final String COLOR_DEFAULT = "#6D28D9";
    private static final String ICONO_DEFAULT = "folder";

    private final EspacioTrabajoRepository espacioRepository;
    private final UsuarioService usuarioService;

    public EspacioService(
            EspacioTrabajoRepository espacioRepository,
            UsuarioService usuarioService) {
        this.espacioRepository = espacioRepository;
        this.usuarioService = usuarioService;
    }

    @Transactional
    public EspacioResponse crear(EspacioRequest request, String correo) {
        Usuario usuario = usuarioService.buscarPorCorreo(correo);
        EspacioTrabajo espacio = new EspacioTrabajo(
                request.nombre().trim(),
                normalizarOpcional(request.descripcion()),
                valorODefault(request.color(), COLOR_DEFAULT),
                valorODefault(request.icono(), ICONO_DEFAULT),
                usuario);
        espacio.agregarMiembro(usuario, RolEspacio.LIDER);
        return toResponse(espacioRepository.save(espacio));
    }

    @Transactional(readOnly = true)
    public List<EspacioResponse> listar(String correo) {
        return espacioRepository
                .findDistinctByMiembrosUsuarioCorreoIgnoreCaseOrderByCreadoEnDesc(
                        correo)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public EspacioResponse obtener(Long id, String correo) {
        return toResponse(buscarAccesible(id, correo));
    }

    @Transactional
    public EspacioResponse actualizar(
            Long id,
            EspacioRequest request,
            String correo) {
        EspacioTrabajo espacio = buscarAccesible(id, correo);
        validarCreador(espacio, correo);
        espacio.actualizar(
                request.nombre().trim(),
                normalizarOpcional(request.descripcion()),
                valorODefault(request.color(), COLOR_DEFAULT),
                valorODefault(request.icono(), ICONO_DEFAULT));
        return toResponse(espacio);
    }

    @Transactional
    public void eliminar(Long id, String correo) {
        EspacioTrabajo espacio = buscarAccesible(id, correo);
        validarCreador(espacio, correo);
        espacioRepository.delete(espacio);
    }

    @Transactional(readOnly = true)
    public EspacioTrabajo buscarAccesible(Long id, String correo) {
        return espacioRepository
                .findDistinctByIdAndMiembrosUsuarioCorreoIgnoreCase(id, correo)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Espacio de trabajo no encontrado"));
    }

    public EspacioResponse toResponse(EspacioTrabajo espacio) {
        return new EspacioResponse(
                espacio.getId(),
                espacio.getNombre(),
                espacio.getDescripcion(),
                espacio.getColor(),
                espacio.getIcono(),
                espacio.getCreadoPor().getId(),
                espacio.getCreadoPor().getNombre(),
                espacio.getMiembros().size(),
                espacio.getProyectos().size(),
                espacio.getCreadoEn());
    }

    private void validarCreador(EspacioTrabajo espacio, String correo) {
        if (!espacio.getCreadoPor().getCorreo().equalsIgnoreCase(correo)) {
            throw new ForbiddenOperationException(
                    "Solo el lider del espacio puede modificarlo");
        }
    }

    private String valorODefault(String value, String defaultValue) {
        return value == null || value.isBlank() ? defaultValue : value.trim();
    }

    private String normalizarOpcional(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
