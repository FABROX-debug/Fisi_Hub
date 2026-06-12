package com.fisihub.security;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.fisihub.model.Usuario;
import com.fisihub.repository.UsuarioRepository;

@Service
public class FisihubUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public FisihubUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String correo)
            throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByCorreoIgnoreCase(correo)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Usuario no encontrado"));

        String[] authorities = usuario.getUsuarioRoles().stream()
                .map(usuarioRol -> "ROLE_" + usuarioRol.getRol().getNombre().name())
                .toArray(String[]::new);

        return User.withUsername(usuario.getCorreo())
                .password(usuario.getPassword())
                .authorities(authorities)
                .disabled(!usuario.isActivo())
                .build();
    }
}

