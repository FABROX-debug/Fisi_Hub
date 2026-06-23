package com.fisihub.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fisihub.dto.InvitacionEspacioRequest;
import com.fisihub.dto.InvitacionEspacioResponse;
import com.fisihub.dto.UsuarioDisponibleResponse;
import com.fisihub.exception.BusinessRuleException;
import com.fisihub.exception.ConflictException;
import com.fisihub.exception.ForbiddenOperationException;
import com.fisihub.exception.ResourceNotFoundException;
import com.fisihub.model.EspacioMiembro;
import com.fisihub.model.EspacioTrabajo;
import com.fisihub.model.EstadoInvitacion;
import com.fisihub.model.InvitacionEspacio;
import com.fisihub.model.RolEspacio;
import com.fisihub.model.Usuario;
import com.fisihub.repository.EspacioMiembroRepository;
import com.fisihub.repository.InvitacionEspacioRepository;
import com.fisihub.repository.UsuarioRepository;

@Service
public class InvitacionEspacioService {

    private static final int DIAS_EXPIRACION = 7;

    private final InvitacionEspacioRepository invitacionRepository;
    private final EspacioMiembroRepository miembroRepository;
    private final UsuarioRepository usuarioRepository;
    private final EspacioService espacioService;
    private final EspacioPermisoService permisoService;
    private final UsuarioService usuarioService;
    private final NotificacionService notificacionService;

    public InvitacionEspacioService(
            InvitacionEspacioRepository invitacionRepository,
            EspacioMiembroRepository miembroRepository,
            UsuarioRepository usuarioRepository,
            EspacioService espacioService,
            EspacioPermisoService permisoService,
            UsuarioService usuarioService,
            NotificacionService notificacionService) {
        this.invitacionRepository = invitacionRepository;
        this.miembroRepository = miembroRepository;
        this.usuarioRepository = usuarioRepository;
        this.espacioService = espacioService;
        this.permisoService = permisoService;
        this.usuarioService = usuarioService;
        this.notificacionService = notificacionService;
    }

    @Transactional
    public InvitacionEspacioResponse crear(
            Long espacioId,
            InvitacionEspacioRequest request,
            String correoActor) {
        EspacioTrabajo espacio = buscarParaGestion(espacioId, correoActor);
        Usuario invitado = usuarioService.buscarPorId(request.usuarioId());
        if (!invitado.isActivo()) {
            throw new BusinessRuleException("El usuario esta inactivo");
        }
        if (miembroRepository.existsByEspacioIdAndUsuarioId(espacioId, invitado.getId())) {
            throw new ConflictException("El usuario ya pertenece al espacio");
        }
        if (invitacionRepository.existsByEspacioIdAndUsuarioIdAndEstado(
                espacioId, invitado.getId(), EstadoInvitacion.PENDIENTE)) {
            throw new ConflictException("Ya existe una invitacion pendiente para este usuario");
        }

        Usuario actor = usuarioService.buscarPorCorreo(correoActor);
        InvitacionEspacio invitacion = invitacionRepository.save(
                new InvitacionEspacio(
                        invitado,
                        espacio,
                        request.rol() == null ? RolEspacio.MIEMBRO : request.rol(),
                        LocalDateTime.now().plusDays(DIAS_EXPIRACION),
                        actor));

        notificacionService.notificarInvitacionEspacio(invitacion);
        return toResponse(invitacion);
    }

    @Transactional
    public List<InvitacionEspacioResponse> listar(Long espacioId, String correoActor) {
        buscarParaGestion(espacioId, correoActor);
        LocalDateTime ahora = LocalDateTime.now();
        return invitacionRepository.findByEspacioIdOrderByCreadoEnDesc(espacioId)
                .stream()
                .peek(inv -> inv.expirarSiCorresponde(ahora))
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public List<UsuarioDisponibleResponse> listarDisponibles(
            Long espacioId,
            String correoActor) {
        espacioService.buscarAccesible(espacioId, correoActor);
        Set<Long> usuariosConInvitacionPendiente = invitacionRepository
                .findByEspacioIdAndEstado(espacioId, EstadoInvitacion.PENDIENTE)
                .stream()
                .map(inv -> inv.getUsuario().getId())
                .collect(java.util.stream.Collectors.toSet());
        return usuarioRepository.findActivosNoMiembrosDeEspacio(espacioId)
                .stream()
                .filter(u -> !usuariosConInvitacionPendiente.contains(u.getId()))
                .map(u -> new UsuarioDisponibleResponse(u.getId(), u.getNombre(), u.getCorreo()))
                .toList();
    }

    @Transactional
    public InvitacionEspacioResponse aceptar(Long invitacionId, String correoActor) {
        InvitacionEspacio invitacion = buscarPorId(invitacionId);
        invitacion.expirarSiCorresponde(LocalDateTime.now());

        Usuario actor = usuarioService.buscarPorCorreo(correoActor);
        if (!invitacion.getUsuario().getId().equals(actor.getId())) {
            throw new ForbiddenOperationException("Esta invitacion no es para ti");
        }
        if (invitacion.getEstado() == EstadoInvitacion.REVOCADA) {
            throw new BusinessRuleException("La invitacion fue revocada");
        }
        if (invitacion.getEstado() == EstadoInvitacion.EXPIRADA) {
            throw new BusinessRuleException("La invitacion ha expirado");
        }
        if (invitacion.getEstado() == EstadoInvitacion.ACEPTADA) {
            throw new BusinessRuleException("La invitacion ya fue aceptada");
        }

        if (!miembroRepository.existsByEspacioIdAndUsuarioId(
                invitacion.getEspacio().getId(), actor.getId())) {
            miembroRepository.save(new EspacioMiembro(
                    invitacion.getEspacio(),
                    actor,
                    invitacion.getRol()));
        }
        invitacion.aceptar();
        return toResponse(invitacion);
    }

    @Transactional
    public InvitacionEspacioResponse rechazar(Long invitacionId, String correoActor) {
        InvitacionEspacio invitacion = buscarPorId(invitacionId);
        invitacion.expirarSiCorresponde(LocalDateTime.now());

        Usuario actor = usuarioService.buscarPorCorreo(correoActor);
        if (!invitacion.getUsuario().getId().equals(actor.getId())) {
            throw new ForbiddenOperationException("Esta invitacion no es para ti");
        }
        if (invitacion.getEstado() != EstadoInvitacion.PENDIENTE) {
            throw new BusinessRuleException(
                    "Solo se pueden rechazar invitaciones pendientes");
        }
        invitacion.revocar();
        return toResponse(invitacion);
    }

    @Transactional
    public InvitacionEspacioResponse reenviar(Long invitacionId, String correoActor) {
        InvitacionEspacio invitacion = buscarPorId(invitacionId);
        buscarParaGestion(invitacion.getEspacio().getId(), correoActor);
        invitacion.expirarSiCorresponde(LocalDateTime.now());

        if (invitacion.getEstado() == EstadoInvitacion.ACEPTADA) {
            throw new BusinessRuleException("Una invitacion aceptada no puede reenviarse");
        }
        if (invitacion.getEstado() == EstadoInvitacion.REVOCADA) {
            throw new BusinessRuleException("Una invitacion revocada no puede reenviarse");
        }

        // Crea una nueva invitación con fecha renovada y elimina la anterior
        Usuario usuarioInvitado = invitacion.getUsuario();
        EspacioTrabajo espacio = invitacion.getEspacio();
        RolEspacio rol = invitacion.getRol();
        Usuario invitadoPor = invitacion.getInvitadoPor();
        notificacionService.eliminarNotificacionInvitacionEspacio(invitacion);
        invitacionRepository.delete(invitacion);
        invitacionRepository.flush();

        InvitacionEspacio renovada = invitacionRepository.save(
                new InvitacionEspacio(
                        usuarioInvitado,
                        espacio,
                        rol,
                        LocalDateTime.now().plusDays(DIAS_EXPIRACION),
                        invitadoPor));
        notificacionService.notificarInvitacionEspacio(renovada);
        return toResponse(renovada);
    }

    @Transactional
    public void revocar(Long invitacionId, String correoActor) {
        InvitacionEspacio invitacion = buscarPorId(invitacionId);
        buscarParaGestion(invitacion.getEspacio().getId(), correoActor);
        if (invitacion.getEstado() == EstadoInvitacion.ACEPTADA) {
            throw new BusinessRuleException("Una invitacion aceptada no puede revocarse");
        }
        invitacion.revocar();
    }

    private EspacioTrabajo buscarParaGestion(Long espacioId, String correoActor) {
        EspacioTrabajo espacio = espacioService.buscarAccesible(espacioId, correoActor);
        if (!permisoService.puedeGestionar(espacio, correoActor)) {
            throw new ForbiddenOperationException(
                    "Solo un lider del espacio o un administrador puede gestionar invitaciones");
        }
        return espacio;
    }

    private InvitacionEspacio buscarPorId(Long id) {
        return invitacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invitacion no encontrada"));
    }

    private InvitacionEspacioResponse toResponse(InvitacionEspacio inv) {
        return new InvitacionEspacioResponse(
                inv.getId(),
                inv.getUsuario().getId(),
                inv.getUsuario().getNombre(),
                inv.getEspacio().getId(),
                inv.getEspacio().getNombre(),
                inv.getRol(),
                inv.getEstado(),
                inv.getExpiraEn(),
                inv.getInvitadoPor().getId(),
                inv.getInvitadoPor().getNombre(),
                inv.getCreadoEn());
    }
}
