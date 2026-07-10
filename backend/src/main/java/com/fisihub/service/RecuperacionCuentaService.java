package com.fisihub.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fisihub.dto.RecuperacionCuentaResponse;
import com.fisihub.dto.ResetPasswordRequest;
import com.fisihub.dto.SolicitarRecuperacionRequest;
import com.fisihub.dto.ValidacionTokenResponse;
import com.fisihub.exception.BusinessRuleException;
import com.fisihub.exception.ResourceNotFoundException;
import com.fisihub.model.RecuperacionCuentaToken;
import com.fisihub.model.Usuario;
import com.fisihub.repository.RecuperacionCuentaTokenRepository;
import com.fisihub.repository.UsuarioRepository;

@Service
public class RecuperacionCuentaService {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final RecuperacionCuentaTokenRepository tokenRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final CorreoRecuperacionService correoService;
    private final String frontendUrl;
    private final long expirationMinutes;
    private final boolean exposeLink;

    public RecuperacionCuentaService(
            RecuperacionCuentaTokenRepository tokenRepository,
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            CorreoRecuperacionService correoService,
            @Value("${app.frontend-url}") String frontendUrl,
            @Value("${app.auth.recovery.expiration-minutes}") long expirationMinutes,
            @Value("${app.auth.recovery.expose-link}") boolean exposeLink) {
        this.tokenRepository = tokenRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.correoService = correoService;
        this.frontendUrl = frontendUrl.replaceAll("/+$", "");
        this.expirationMinutes = expirationMinutes;
        this.exposeLink = exposeLink;
    }

    @Transactional
    public RecuperacionCuentaResponse solicitar(
            SolicitarRecuperacionRequest request) {
        String correo = request.correo().trim().toLowerCase(Locale.ROOT);
        Usuario usuario = usuarioRepository.findByCorreoIgnoreCase(correo)
                .orElse(null);

        if (usuario == null || !usuario.isActivo()) {
            return new RecuperacionCuentaResponse(
                    "Si el correo existe, enviaremos un enlace para recuperar la cuenta.",
                    null);
        }

        String rawToken = newToken();
        String tokenHash = hash(rawToken);
        LocalDateTime expiration = LocalDateTime.now()
                .plusMinutes(expirationMinutes);

        RecuperacionCuentaToken token = tokenRepository.findByUsuario(usuario)
                .orElseGet(() -> new RecuperacionCuentaToken(
                        usuario,
                        tokenHash,
                        expiration));
        token.reemplazar(tokenHash, expiration);
        tokenRepository.save(token);

        String resetUrl = frontendUrl + "/reset-password/" + rawToken;
        correoService.enviar(usuario, resetUrl);
        return new RecuperacionCuentaResponse(
                "Si el correo existe, enviaremos un enlace para recuperar la cuenta.",
                exposeLink ? resetUrl : null);
    }

    @Transactional(readOnly = true)
    public ValidacionTokenResponse validar(String token) {
        RecuperacionCuentaToken resetToken = buscarVigente(token);
        return new ValidacionTokenResponse(
                true,
                resetToken.getUsuario().getCorreo(),
                "Token valido");
    }

    @Transactional
    public RecuperacionCuentaResponse restablecer(
            ResetPasswordRequest request) {
        RecuperacionCuentaToken resetToken = buscarVigente(request.token());
        resetToken.getUsuario().actualizarPassword(
                passwordEncoder.encode(request.password()));
        resetToken.marcarUsado(LocalDateTime.now());
        return new RecuperacionCuentaResponse(
                "La contrasena fue actualizada correctamente.",
                null);
    }

    private RecuperacionCuentaToken buscarVigente(String rawToken) {
        RecuperacionCuentaToken resetToken = tokenRepository
                .findByTokenHash(hash(rawToken))
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Token de recuperacion no encontrado"));
        if (resetToken.fueUsado()) {
            throw new BusinessRuleException(
                    "Este enlace de recuperacion ya fue utilizado");
        }
        if (resetToken.estaExpirado(LocalDateTime.now())) {
            throw new BusinessRuleException(
                    "Este enlace de recuperacion ha expirado");
        }
        if (!resetToken.getUsuario().isActivo()) {
            throw new BusinessRuleException(
                    "La cuenta asociada esta inactiva");
        }
        return resetToken;
    }

    private String newToken() {
        byte[] bytes = new byte[32];
        SECURE_RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String hash(String token) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 no disponible", exception);
        }
    }
}
