package com.fisihub.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.fisihub.model.Usuario;

@Service
public class CorreoRecuperacionService {

    private static final Logger log =
            LoggerFactory.getLogger(CorreoRecuperacionService.class);

    private final JavaMailSender mailSender;
    private final String remitente;

    public CorreoRecuperacionService(
            JavaMailSender mailSender,
            @Value("${app.mail.from}") String remitente) {
        this.mailSender = mailSender;
        this.remitente = remitente;
    }

    public void enviar(Usuario usuario, String resetUrl) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(remitente);
            message.setTo(usuario.getCorreo());
            message.setSubject("Recuperacion de cuenta FISIHUB");
            message.setText("""
                    Hola %s,

                    Recibimos una solicitud para restablecer tu contrasena.
                    Usa este enlace:

                    %s

                    Si no solicitaste este cambio, ignora este mensaje.
                    """.formatted(usuario.getNombre(), resetUrl));
            mailSender.send(message);
        } catch (Exception exception) {
            log.warn("No se pudo enviar correo de recuperacion a {}: {}",
                    usuario.getCorreo(), exception.getMessage());
        }
    }
}
