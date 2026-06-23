package com.fisihub.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fisihub.dto.NotificacionResponse;
import com.fisihub.exception.ResourceNotFoundException;
import com.fisihub.model.EstadoInvitacion;
import com.fisihub.model.EstadoTarea;
import com.fisihub.model.InvitacionEspacio;
import com.fisihub.model.Notificacion;
import com.fisihub.model.Proyecto;
import com.fisihub.model.Tarea;
import com.fisihub.model.TipoNotificacion;
import com.fisihub.model.Usuario;
import com.fisihub.repository.InvitacionEspacioRepository;
import com.fisihub.repository.NotificacionRepository;
import com.fisihub.repository.TareaRepository;

@Service
public class NotificacionService {

    private final NotificacionRepository notificacionRepository;
    private final InvitacionEspacioRepository invitacionRepository;
    private final TareaRepository tareaRepository;
    private final UsuarioService usuarioService;

    public NotificacionService(
            NotificacionRepository notificacionRepository,
            InvitacionEspacioRepository invitacionRepository,
            TareaRepository tareaRepository,
            UsuarioService usuarioService) {
        this.notificacionRepository = notificacionRepository;
        this.invitacionRepository = invitacionRepository;
        this.tareaRepository = tareaRepository;
        this.usuarioService = usuarioService;
    }

    @Transactional
    public List<NotificacionResponse> listar(String correo) {
        Usuario usuario = usuarioService.buscarPorCorreo(correo);
        generarVencimientosManana(usuario);
        return notificacionRepository
                .findByUsuarioCorreoIgnoreCaseOrderByCreadoEnDesc(correo)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public NotificacionResponse marcarLeida(Long id, String correo) {
        Notificacion notificacion = notificacionRepository
                .findByIdAndUsuarioCorreoIgnoreCase(id, correo)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Notificacion no encontrada"));
        notificacion.marcarLeida();
        return toResponse(notificacion);
    }

    @Transactional
    public void marcarTodasLeidas(String correo) {
        notificacionRepository
                .findByUsuarioCorreoIgnoreCaseAndLeidaFalse(correo)
                .forEach(Notificacion::marcarLeida);
    }

    @Transactional
    public void notificarAsignacion(Tarea tarea) {
        if (tarea.getResponsable() == null) {
            return;
        }
        crearSiNoExiste(
                tarea.getResponsable(),
                TipoNotificacion.ASIGNACION_TAREA,
                "Te asignaron la tarea \"" + tarea.getTitulo()
                        + "\" en " + tarea.getProyecto().getNombre(),
                tarea.getId());
    }

    @Transactional
    public void notificarInvitacionEspacio(InvitacionEspacio invitacion) {
        notificacionRepository.save(
                new Notificacion(
                        invitacion.getUsuario(),
                        TipoNotificacion.INVITACION_ESPACIO,
                        invitacion.getInvitadoPor().getNombre()
                                + " te invito al espacio \""
                                + invitacion.getEspacio().getNombre()
                                + "\" como " + invitacion.getRol().name(),
                        invitacion.getId()));
    }

    @Transactional
    public void eliminarNotificacionInvitacionEspacio(InvitacionEspacio invitacion) {
        notificacionRepository.deleteByUsuarioIdAndTipoAndReferenciaId(
                invitacion.getUsuario().getId(),
                TipoNotificacion.INVITACION_ESPACIO,
                invitacion.getId());
    }

    @Transactional
    public void notificarMiembroAgregado(Proyecto proyecto, Usuario usuario) {
        crearSiNoExiste(
                usuario,
                TipoNotificacion.MIEMBRO_PROYECTO,
                "Fuiste agregado al proyecto \"" + proyecto.getNombre() + "\"",
                proyecto.getId());
    }

    private void generarVencimientosManana(Usuario usuario) {
        tareaRepository.findByResponsableIdAndFechaLimiteAndEstadoNot(
                        usuario.getId(),
                        LocalDate.now().plusDays(1),
                        EstadoTarea.COMPLETADA)
                .forEach(tarea -> crearSiNoExiste(
                        usuario,
                        TipoNotificacion.TAREA_VENCE_MANANA,
                        "La tarea \"" + tarea.getTitulo() + "\" vence manana",
                        tarea.getId()));
    }

    private void crearSiNoExiste(
            Usuario usuario,
            TipoNotificacion tipo,
            String mensaje,
            Long referenciaId) {
        if (!notificacionRepository.existsByUsuarioIdAndTipoAndReferenciaId(
                usuario.getId(),
                tipo,
                referenciaId)) {
            notificacionRepository.save(
                    new Notificacion(usuario, tipo, mensaje, referenciaId));
        }
    }

    private NotificacionResponse toResponse(Notificacion notificacion) {
        return new NotificacionResponse(
                notificacion.getId(),
                notificacion.getTipo(),
                notificacion.getMensaje(),
                notificacion.getReferenciaId(),
                resolveInvitationState(notificacion),
                notificacion.isLeida(),
                notificacion.getCreadoEn());
    }

    private EstadoInvitacion resolveInvitationState(Notificacion notificacion) {
        if (notificacion.getTipo() != TipoNotificacion.INVITACION_ESPACIO) {
            return null;
        }
        return invitacionRepository.findById(notificacion.getReferenciaId())
                .map(InvitacionEspacio::getEstado)
                .orElse(null);
    }
}
