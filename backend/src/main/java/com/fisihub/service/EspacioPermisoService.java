package com.fisihub.service;

import org.springframework.stereotype.Service;

import com.fisihub.model.EspacioTrabajo;
import com.fisihub.model.RolEspacio;
import com.fisihub.repository.EspacioMiembroRepository;

@Service
public class EspacioPermisoService {

    private final EspacioMiembroRepository miembroRepository;
    private final UsuarioService usuarioService;

    public EspacioPermisoService(
            EspacioMiembroRepository miembroRepository,
            UsuarioService usuarioService) {
        this.miembroRepository = miembroRepository;
        this.usuarioService = usuarioService;
    }

    public boolean puedeGestionar(EspacioTrabajo espacio, String correo) {
        return usuarioService.esAdmin(correo)
                || espacio.getCreadoPor().getCorreo().equalsIgnoreCase(correo)
                || miembroRepository
                        .findByEspacioIdAndUsuarioCorreoIgnoreCase(
                                espacio.getId(),
                                correo)
                        .map(miembro -> miembro.getRol() == RolEspacio.LIDER)
                        .orElse(false);
    }
}
