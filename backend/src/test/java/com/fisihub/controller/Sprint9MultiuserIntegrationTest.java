package com.fisihub.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fisihub.model.EstadoInvitacion;
import com.fisihub.model.InvitacionEspacio;
import com.fisihub.model.RolEspacio;
import com.fisihub.repository.ComentarioRepository;
import com.fisihub.repository.EspacioMiembroRepository;
import com.fisihub.repository.EspacioTrabajoRepository;
import com.fisihub.repository.HistorialActividadRepository;
import com.fisihub.repository.InvitacionEspacioRepository;
import com.fisihub.repository.MiembroProyectoRepository;
import com.fisihub.repository.NotificacionRepository;
import com.fisihub.repository.ProyectoRepository;
import com.fisihub.repository.TareaRepository;
import com.fisihub.repository.UsuarioRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class Sprint9MultiuserIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private InvitacionEspacioRepository invitacionRepository;

    @Autowired
    private ComentarioRepository comentarioRepository;

    @Autowired
    private NotificacionRepository notificacionRepository;

    @Autowired
    private HistorialActividadRepository historialRepository;

    @Autowired
    private TareaRepository tareaRepository;

    @Autowired
    private MiembroProyectoRepository miembroProyectoRepository;

    @Autowired
    private ProyectoRepository proyectoRepository;

    @Autowired
    private EspacioMiembroRepository espacioMiembroRepository;

    @Autowired
    private EspacioTrabajoRepository espacioRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @BeforeEach
    void cleanData() {
        invitacionRepository.deleteAll();
        notificacionRepository.deleteAll();
        comentarioRepository.deleteAll();
        historialRepository.deleteAll();
        tareaRepository.deleteAll();
        miembroProyectoRepository.deleteAll();
        proyectoRepository.deleteAll();
        espacioMiembroRepository.deleteAll();
        espacioRepository.deleteAll();
        usuarioRepository.deleteAll();
    }

    @Test
    void managesInAppInvitationLifecycleAndNotifications()
            throws Exception {
        AuthData owner = register(
                "Lider Invitaciones",
                "invite.owner@fisihub.local");
        AuthData member = register(
                "Miembro Invitado",
                "invite.member@fisihub.local");
        long workspaceId = createWorkspace(owner.token());

        JsonNode invitation = createInvitation(
                workspaceId,
                owner.token(),
                member.userId(),
                "MIEMBRO");

        long invitationId = invitation.get("id").asLong();

        mockMvc.perform(post("/api/espacios/{id}/invitaciones", workspaceId)
                        .header("Authorization", bearer(owner.token()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "usuarioId": %d,
                                  "rol": "MIEMBRO"
                                }
                                """.formatted(member.userId())))
                .andExpect(status().isConflict());

        mockMvc.perform(get("/api/espacios/{id}/invitaciones", workspaceId)
                        .header("Authorization", bearer(owner.token())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(invitationId))
                .andExpect(jsonPath("$[0].estado").value("PENDIENTE"));

        mockMvc.perform(get("/api/notificaciones")
                        .header("Authorization", bearer(member.token())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].tipo").value("INVITACION_ESPACIO"))
                .andExpect(jsonPath("$[0].referenciaId").value(invitationId))
                .andExpect(jsonPath("$[0].leida").value(false));

        mockMvc.perform(post("/api/invitaciones/{id}/aceptar", invitationId))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(post("/api/invitaciones/{id}/aceptar", invitationId)
                        .header("Authorization", bearer(member.token())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("ACEPTADA"));

        mockMvc.perform(get("/api/notificaciones")
                        .header("Authorization", bearer(member.token())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].invitacionEstado").value("ACEPTADA"));

        mockMvc.perform(post("/api/invitaciones/{id}/aceptar", invitationId)
                        .header("Authorization", bearer(member.token())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("La invitacion ya fue aceptada"));

        assertThat(espacioMiembroRepository
                .findByEspacioIdOrderByUsuarioNombreAsc(workspaceId))
                .hasSize(2);

        mockMvc.perform(get("/api/espacios/{id}/miembros", workspaceId)
                        .header("Authorization", bearer(member.token())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.miembros.length()").value(2));
    }

    @Test
    void rejectsRevokedExpiredAndCrossAccountInvitations()
            throws Exception {
        AuthData owner = register(
                "Lider Seguridad",
                "secure.owner@fisihub.local");
        AuthData invited = register(
                "Invitado Seguridad",
                "secure.invited@fisihub.local");
        AuthData other = register(
                "Otra Cuenta",
                "secure.other@fisihub.local");
        long workspaceId = createWorkspace(owner.token());

        JsonNode invitation = createInvitation(
                workspaceId,
                owner.token(),
                invited.userId(),
                "MIEMBRO");
        long invitationId = invitation.get("id").asLong();

        mockMvc.perform(post("/api/invitaciones/{id}/aceptar", invitationId)
                        .header("Authorization", bearer(other.token())))
                .andExpect(status().isForbidden());

        mockMvc.perform(delete("/api/invitaciones/{id}",
                        invitation.get("id").asLong())
                        .header("Authorization", bearer(owner.token())))
                .andExpect(status().isNoContent());

        mockMvc.perform(post("/api/invitaciones/{id}/aceptar", invitationId)
                        .header("Authorization", bearer(invited.token())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("La invitacion fue revocada"));

        var workspace = espacioRepository.findById(workspaceId).orElseThrow();
        var inviter = usuarioRepository.findById(owner.userId()).orElseThrow();
        var invitedUser = usuarioRepository.findById(invited.userId()).orElseThrow();
        InvitacionEspacio expiredInvitation = invitacionRepository.saveAndFlush(
                new InvitacionEspacio(
                invitedUser,
                workspace,
                RolEspacio.MIEMBRO,
                LocalDateTime.now().minusMinutes(1),
                inviter));

        mockMvc.perform(get("/api/espacios/{id}/invitaciones", workspaceId)
                        .header("Authorization", bearer(owner.token())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].estado").value("EXPIRADA"));
        mockMvc.perform(post("/api/invitaciones/{id}/aceptar", expiredInvitation.getId())
                        .header("Authorization", bearer(invited.token())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("La invitacion ha expirado"));

        JsonNode rejectedInvitation = createInvitation(
                workspaceId,
                owner.token(),
                invited.userId(),
                "MIEMBRO");
        mockMvc.perform(post("/api/invitaciones/{id}/rechazar",
                        rejectedInvitation.get("id").asLong())
                        .header("Authorization", bearer(invited.token())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("REVOCADA"));

        JsonNode reissuedInvitation = createInvitation(
                workspaceId,
                owner.token(),
                invited.userId(),
                "MIEMBRO");
        long originalInvitationId = reissuedInvitation.get("id").asLong();

        JsonNode renewedInvitation = objectMapper.readTree(mockMvc.perform(
                        post("/api/invitaciones/{id}/reenviar", originalInvitationId)
                                .header("Authorization", bearer(owner.token())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andReturn().getResponse().getContentAsString());

        mockMvc.perform(get("/api/notificaciones")
                        .header("Authorization", bearer(invited.token())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].referenciaId")
                        .value(renewedInvitation.get("id").asLong()))
                .andExpect(jsonPath("$[0].invitacionEstado").value("PENDIENTE"));
    }

    @Test
    void enforcesProjectSelectionAndTaskAssignmentPermissions()
            throws Exception {
        AuthData owner = register(
                "Lider Permisos",
                "permissions.owner@fisihub.local");
        AuthData member = register(
                "Miembro Permisos",
                "permissions.member@fisihub.local");
        AuthData outsider = register(
                "Externo Permisos",
                "permissions.outsider@fisihub.local");
        long workspaceId = createWorkspace(owner.token());
        acceptInvitation(owner, member, workspaceId);
        long projectId = createProject(owner.token(), workspaceId);

        mockMvc.perform(post("/api/proyectos/{id}/miembros", projectId)
                        .header("Authorization", bearer(owner.token()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"usuarioId":%d,"rol":"MIEMBRO"}
                                """.formatted(member.userId())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.activo").value(true));

        mockMvc.perform(post("/api/proyectos/{id}/miembros", projectId)
                        .header("Authorization", bearer(owner.token()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"usuarioId":%d}
                                """.formatted(outsider.userId())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(
                        "El usuario debe pertenecer al espacio antes de agregarse al proyecto"));

        JsonNode task = objectMapper.readTree(mockMvc.perform(
                        post("/api/tareas")
                                .header("Authorization", bearer(owner.token()))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "titulo":"Tarea asignada",
                                          "proyectoId":%d,
                                          "responsableId":%d
                                        }
                                        """.formatted(
                                        projectId,
                                        member.userId())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.puedeReasignar").value(true))
                .andReturn().getResponse().getContentAsString());
        long taskId = task.get("id").asLong();

        mockMvc.perform(get("/api/tareas/{id}", taskId)
                        .header("Authorization", bearer(member.token())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.puedeEditar").value(true))
                .andExpect(jsonPath("$.puedeEliminar").value(false))
                .andExpect(jsonPath("$.puedeReasignar").value(false));

        mockMvc.perform(put("/api/tareas/{id}", taskId)
                        .header("Authorization", bearer(member.token()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "titulo":"Tarea actualizada por responsable",
                                  "responsableId":%d,
                                  "estado":"EN_PROCESO",
                                  "prioridad":"ALTA"
                                }
                                """.formatted(member.userId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("EN_PROCESO"));

        mockMvc.perform(put("/api/tareas/{id}", taskId)
                        .header("Authorization", bearer(member.token()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "titulo":"Reasignacion no permitida",
                                  "responsableId":%d
                                }
                                """.formatted(owner.userId())))
                .andExpect(status().isForbidden());

        mockMvc.perform(delete("/api/tareas/{id}", taskId)
                        .header("Authorization", bearer(member.token())))
                .andExpect(status().isForbidden());

        mockMvc.perform(patch("/api/tareas/{id}/estado", taskId)
                        .header("Authorization", bearer(outsider.token()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"estado\":\"COMPLETADA\"}"))
                .andExpect(status().isNotFound());

        mockMvc.perform(delete("/api/tareas/{id}", taskId)
                        .header("Authorization", bearer(owner.token())))
                .andExpect(status().isNoContent());
    }

    private void acceptInvitation(
            AuthData owner,
            AuthData member,
            long workspaceId) throws Exception {
        JsonNode invitation = createInvitation(
                workspaceId,
                owner.token(),
                member.userId(),
                "MIEMBRO");
        mockMvc.perform(post("/api/invitaciones/{id}/aceptar",
                        invitation.get("id").asLong())
                        .header("Authorization", bearer(member.token())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("ACEPTADA"));
    }

    private JsonNode createInvitation(
            long workspaceId,
            String token,
            long userId,
            String role) throws Exception {
        return objectMapper.readTree(mockMvc.perform(
                        post("/api/espacios/{id}/invitaciones", workspaceId)
                                .header("Authorization", bearer(token))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "usuarioId": %d,
                                          "rol": "%s"
                                        }
                                        """.formatted(userId, role)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.usuarioId").value(userId))
                .andExpect(jsonPath("$.estado").value(
                        EstadoInvitacion.PENDIENTE.name()))
                .andReturn().getResponse().getContentAsString());
    }

    private AuthData register(String name, String email) throws Exception {
        JsonNode response = objectMapper.readTree(mockMvc.perform(
                        post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "nombre":"%s",
                                          "correo":"%s",
                                          "password":"Password123"
                                        }
                                        """.formatted(name, email)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString());
        return new AuthData(
                response.get("token").asText(),
                response.get("usuario").get("id").asLong(),
                email);
    }

    private long createWorkspace(String token) throws Exception {
        JsonNode workspace = objectMapper.readTree(mockMvc.perform(
                        post("/api/espacios")
                                .header("Authorization", bearer(token))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"nombre\":\"Equipo Sprint 9\"}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString());
        return workspace.get("id").asLong();
    }

    private long createProject(String token, long workspaceId)
            throws Exception {
        JsonNode project = objectMapper.readTree(mockMvc.perform(
                        post("/api/proyectos")
                                .header("Authorization", bearer(token))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "nombre":"Proyecto Sprint 9",
                                          "espacioId":%d
                                        }
                                        """.formatted(workspaceId)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString());
        return project.get("id").asLong();
    }

    private String bearer(String token) {
        return "Bearer " + token;
    }

    private record AuthData(String token, long userId, String email) {
    }
}
