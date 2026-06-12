package com.fisihub.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fisihub.dto.AuthResponse;
import com.fisihub.dto.LoginRequest;
import com.fisihub.dto.RegisterRequest;
import com.fisihub.dto.UsuarioResponse;
import com.fisihub.model.Usuario;
import com.fisihub.security.FisihubUserDetailsService;
import com.fisihub.security.JwtService;

@Service
public class AuthService {

    private final UsuarioService usuarioService;
    private final AuthenticationManager authenticationManager;
    private final FisihubUserDetailsService userDetailsService;
    private final JwtService jwtService;

    public AuthService(
            UsuarioService usuarioService,
            AuthenticationManager authenticationManager,
            FisihubUserDetailsService userDetailsService,
            JwtService jwtService) {
        this.usuarioService = usuarioService;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        Usuario usuario = usuarioService.registrar(request);
        UserDetails userDetails =
                userDetailsService.loadUserByUsername(usuario.getCorreo());
        return buildResponse(usuario, userDetails);
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.correo().trim(),
                        request.password()));
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Usuario usuario = usuarioService.buscarPorCorreo(
                userDetails.getUsername());
        return buildResponse(usuario, userDetails);
    }

    @Transactional(readOnly = true)
    public UsuarioResponse me(String correo) {
        return usuarioService.toResponse(
                usuarioService.buscarPorCorreo(correo));
    }

    private AuthResponse buildResponse(
            Usuario usuario,
            UserDetails userDetails) {
        return new AuthResponse(
                jwtService.generateToken(userDetails),
                "Bearer",
                jwtService.getExpirationMs(),
                usuarioService.toResponse(usuario));
    }
}

