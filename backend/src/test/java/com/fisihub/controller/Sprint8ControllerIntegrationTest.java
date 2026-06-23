package com.fisihub.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fisihub.model.RolNombre;
import com.fisihub.model.Usuario;
import com.fisihub.model.EspacioMiembro;
import com.fisihub.model.RolEspacio;
import com.fisihub.repository.ComentarioRepository;
import com.fisihub.repository.EspacioMiembroRepository;
import com.fisihub.repository.EspacioTrabajoRepository;
import com.fisihub.repository.HistorialActividadRepository;
import com.fisihub.repository.MiembroProyectoRepository;
import com.fisihub.repository.NotificacionRepository;
import com.fisihub.repository.ProyectoRepository;
import com.fisihub.repository.RolRepository;
import com.fisihub.repository.TareaRepository;
import com.fisihub.repository.UsuarioRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class Sprint8ControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private NotificacionRepository notificacionRepository;

    @Autowired
    private ComentarioRepository comentarioRepository;

    @Autowired
    private HistorialActividadRepository historialRepository;

    @Autowired
    private TareaRepository tareaRepository;

    @Autowired
    private MiembroProyectoRepository miembroRepository;

    @Autowired
    private ProyectoRepository proyectoRepository;

    @Autowired
    private EspacioMiembroRepository espacioMiembroRepository;

    @Autowired
    private EspacioTrabajoRepository espacioRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    @BeforeEach
    void cleanData() {
        notificacionRepository.deleteAll();
        comentarioRepository.deleteAll();
        historialRepository.deleteAll();
        tareaRepository.deleteAll();
        miembroRepository.deleteAll();
        proyectoRepository.deleteAll();
        espacioMiembroRepository.deleteAll();
        espacioRepository.deleteAll();
        usuarioRepository.deleteAll();
    }

    @Test
    void requiresAuthenticationAndAdminRole() throws Exception {
        mockMvc.perform(get("/api/notificaciones"))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/proyectos/1/reportes/avance"))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/admin/usuarios"))
                .andExpect(status().isUnauthorized());

        AuthData member = register("Miembro", "member.s8@fisihub.local");
        mockMvc.perform(get("/api/admin/usuarios")
                        .header("Authorization", bearer(member.token())))
                .andExpect(status().isForbidden());
    }

    @Test
    void handlesNotificationsReportsAndAdministration() throws Exception {
        AuthData owner = register("Lider Sprint 8", "owner.s8@fisihub.local");
        AuthData member = register("Miembro Sprint 8", "member.s8@fisihub.local");
        AuthData admin = register("Admin Sprint 8", "admin.s8@fisihub.local");
        grantAdmin(admin.email());

        long projectId = createProject(owner.token());
        addWorkspaceMember(projectId, member.userId());
        addMember(owner.token(), projectId, member.email());
        long pendingTaskId = createTask(
                owner.token(),
                projectId,
                member.userId(),
                "Tarea que vence manana",
                "PENDIENTE",
                LocalDate.now().plusDays(1));
        createTask(
                owner.token(),
                projectId,
                owner.userId(),
                "Tarea terminada",
                "COMPLETADA",
                LocalDate.now().plusDays(2));
        createTask(
                owner.token(),
                projectId,
                member.userId(),
                "Tarea en curso",
                "EN_PROCESO",
                LocalDate.now().plusDays(3));

        String notificationsJson = mockMvc.perform(get("/api/notificaciones")
                        .header("Authorization", bearer(member.token())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.tipo == 'MIEMBRO_PROYECTO')]")
                        .isNotEmpty())
                .andExpect(jsonPath("$[?(@.tipo == 'ASIGNACION_TAREA')]")
                        .isNotEmpty())
                .andExpect(jsonPath("$[?(@.tipo == 'TAREA_VENCE_MANANA')]")
                        .isNotEmpty())
                .andReturn().getResponse().getContentAsString();
        JsonNode notifications = objectMapper.readTree(notificationsJson);

        mockMvc.perform(patch("/api/notificaciones/{id}/leida",
                        notifications.get(0).get("id").asLong())
                        .header("Authorization", bearer(member.token())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.leida").value(true));

        mockMvc.perform(patch("/api/notificaciones/leer-todas")
                        .header("Authorization", bearer(member.token())))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/notificaciones")
                        .header("Authorization", bearer(member.token())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.leida == false)]").isEmpty());

        mockMvc.perform(get("/api/proyectos/{id}/reportes/avance", projectId)
                        .header("Authorization", bearer(owner.token())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalTareas").value(3))
                .andExpect(jsonPath("$.tareasCompletadas").value(1))
                .andExpect(jsonPath("$.tareasPendientes").value(1))
                .andExpect(jsonPath("$.tareasEnProceso").value(1))
                .andExpect(jsonPath("$.porcentajeAvance").value(33))
                .andExpect(jsonPath("$.productividadMiembros.length()")
                        .value(2));

        mockMvc.perform(get("/api/admin/estadisticas")
                        .header("Authorization", bearer(admin.token())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalUsuarios").value(3))
                .andExpect(jsonPath("$.totalProyectos").value(1))
                .andExpect(jsonPath("$.totalTareas").value(3));

        mockMvc.perform(get("/api/admin/proyectos")
                        .header("Authorization", bearer(admin.token())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(projectId));

        mockMvc.perform(patch("/api/admin/usuarios/{id}/desactivar",
                        member.userId())
                        .header("Authorization", bearer(admin.token())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.activo").value(false));

        mockMvc.perform(get("/api/notificaciones")
                        .header("Authorization", bearer(member.token())))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(patch("/api/admin/usuarios/{id}/activar",
                        member.userId())
                        .header("Authorization", bearer(admin.token())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.activo").value(true));

        mockMvc.perform(get("/api/tareas/{id}", pendingTaskId)
                        .header("Authorization", bearer(member.token())))
                .andExpect(status().isOk());
    }

    private AuthData register(String name, String email) throws Exception {
        JsonNode response = objectMapper.readTree(mockMvc.perform(
                        post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "nombre": "%s",
                                          "correo": "%s",
                                          "password": "Password123"
                                        }
                                        """.formatted(name, email)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString());
        return new AuthData(
                response.get("token").asText(),
                response.get("usuario").get("id").asLong(),
                email);
    }

    private void grantAdmin(String email) {
        Usuario usuario = usuarioRepository.findByCorreoIgnoreCase(email)
                .orElseThrow();
        usuario.agregarRol(rolRepository.findByNombre(RolNombre.ADMIN)
                .orElseThrow());
        usuarioRepository.saveAndFlush(usuario);
    }

    private long createProject(String token) throws Exception {
        JsonNode workspace = objectMapper.readTree(mockMvc.perform(
                        post("/api/espacios")
                                .header("Authorization", bearer(token))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"nombre\":\"Espacio Sprint 8\"}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString());
        JsonNode project = objectMapper.readTree(mockMvc.perform(
                        post("/api/proyectos")
                                .header("Authorization", bearer(token))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "nombre": "Proyecto Sprint 8",
                                          "espacioId": %d
                                        }
                                        """.formatted(workspace.get("id").asLong())))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString());
        return project.get("id").asLong();
    }

    private void addMember(
            String token,
            long projectId,
            String email) throws Exception {
        mockMvc.perform(post("/api/proyectos/{id}/miembros", projectId)
                        .header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"correo":"%s","rol":"MIEMBRO"}
                                """.formatted(email)))
                .andExpect(status().isCreated());
    }

    private void addWorkspaceMember(long projectId, long userId) {
        var project = proyectoRepository.findById(projectId).orElseThrow();
        var user = usuarioRepository.findById(userId).orElseThrow();
        espacioMiembroRepository.saveAndFlush(new EspacioMiembro(
                project.getEspacio(),
                user,
                RolEspacio.MIEMBRO));
    }

    private long createTask(
            String token,
            long projectId,
            long responsibleId,
            String title,
            String status,
            LocalDate dueDate) throws Exception {
        JsonNode task = objectMapper.readTree(mockMvc.perform(
                        post("/api/tareas")
                                .header("Authorization", bearer(token))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "titulo": "%s",
                                          "proyectoId": %d,
                                          "responsableId": %d,
                                          "estado": "%s",
                                          "fechaLimite": "%s"
                                        }
                                        """.formatted(
                                                title,
                                                projectId,
                                                responsibleId,
                                                status,
                                                dueDate)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString());
        return task.get("id").asLong();
    }

    private String bearer(String token) {
        return "Bearer " + token;
    }

    private record AuthData(String token, long userId, String email) {
    }
}
