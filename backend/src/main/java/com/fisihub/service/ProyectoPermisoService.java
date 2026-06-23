package com.fisihub.service;

import org.springframework.stereotype.Service;

import com.fisihub.model.Proyecto;
import com.fisihub.model.RolProyecto;
import com.fisihub.repository.MiembroProyectoRepository;

@Service
public class ProyectoPermisoService {

    private final MiembroProyectoRepository miembroRepository;
    private final UsuarioService usuarioService;

    public ProyectoPermisoService(
            MiembroProyectoRepository miembroRepository,
            UsuarioService usuarioService) {
        this.miembroRepository = miembroRepository;
        this.usuarioService = usuarioService;
    }

    public boolean puedeGestionar(Proyecto proyecto, String correo) {
        return usuarioService.esAdmin(correo)
                || proyecto.getLider().getCorreo().equalsIgnoreCase(correo)
                || miembroRepository
                        .findByProyectoIdAndUsuarioCorreoIgnoreCase(
                                proyecto.getId(),
                                correo)
                        .map(miembro -> miembro.getRol() == RolProyecto.LIDER)
                        .orElse(false);
    }
}
