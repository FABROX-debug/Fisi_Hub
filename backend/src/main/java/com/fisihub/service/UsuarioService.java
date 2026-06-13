package com.fisihub.service;

import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fisihub.dto.RegisterRequest;
import com.fisihub.dto.UsuarioResponse;
import com.fisihub.exception.ConflictException;
import com.fisihub.exception.ResourceNotFoundException;
import com.fisihub.model.Rol;
import com.fisihub.model.RolNombre;
import com.fisihub.model.Usuario;
import com.fisihub.repository.RolRepository;
import com.fisihub.repository.UsuarioRepository;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(
            UsuarioRepository usuarioRepository,
            RolRepository rolRepository,
            PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Usuario registrar(RegisterRequest request) {
        String correo = normalizarCorreo(request.correo());
        if (usuarioRepository.existsByCorreoIgnoreCase(correo)) {
            throw new ConflictException("El correo ya esta registrado");
        }

        Rol rolMiembro = rolRepository.findByNombre(RolNombre.MIEMBRO)
                .orElseThrow(() -> new IllegalStateException(
                        "El rol MIEMBRO no esta configurado"));

        Usuario usuario = new Usuario(
                request.nombre().trim(),
                correo,
                passwordEncoder.encode(request.password()));
        usuario.agregarRol(rolMiembro);
        return usuarioRepository.save(usuario);
    }

    @Transactional(readOnly = true)
    public Usuario buscarPorCorreo(String correo) {
        return usuarioRepository.findByCorreoIgnoreCase(normalizarCorreo(correo))
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Usuario no encontrado"));
    }

    @Transactional(readOnly = true)
    public boolean esAdmin(String correo) {
        return buscarPorCorreo(correo).getUsuarioRoles().stream()
                .anyMatch(usuarioRol ->
                        usuarioRol.getRol().getNombre() == RolNombre.ADMIN);
    }

    public UsuarioResponse toResponse(Usuario usuario) {
        Set<String> roles = usuario.getUsuarioRoles().stream()
                .map(usuarioRol -> usuarioRol.getRol().getNombre().name())
                .sorted()
                .collect(java.util.stream.Collectors.toCollection(
                        LinkedHashSet::new));

        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getCorreo(),
                usuario.isActivo(),
                roles,
                usuario.getCreadoEn());
    }

    private String normalizarCorreo(String correo) {
        return correo.trim().toLowerCase(Locale.ROOT);
    }
}
