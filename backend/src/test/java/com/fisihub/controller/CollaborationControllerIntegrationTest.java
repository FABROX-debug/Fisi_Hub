package com.fisihub.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import com.fisihub.repository.ComentarioRepository;
import com.fisihub.repository.EspacioMiembroRepository;
import com.fisihub.repository.EspacioTrabajoRepository;
import com.fisihub.repository.HistorialActividadRepository;
import com.fisihub.repository.MiembroProyectoRepository;
import com.fisihub.repository.ProyectoRepository;
import com.fisihub.repository.TareaRepository;
import com.fisihub.repository.UsuarioRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CollaborationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ComentarioRepository comentarioRepository;

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
    void requiresJwtForCollaborationEndpoints() throws Exception {
        mockMvc.perform(get("/api/proyectos/1/miembros"))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/tareas/1/comentarios"))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/proyectos/1/actividad"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void managesMembersCommentsAndActivityWithProjectPermissions()
            throws Exception {
        AuthData owner = register(
                "Lider Colaboracion",
                "collab.owner@fisihub.local");
        AuthData member = register(
                "Miembro Colaboracion",
                "collab.member@fisihub.local");
        AuthData outsider = register(
                "Usuario Externo",
                "collab.outsider@fisihub.local");
        long projectId = createProject(owner.token());

        mockMvc.perform(get("/api/proyectos/{id}/miembros", projectId)
                        .header("Authorization", bearer(owner.token())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.puedeGestionar").value(true))
                .andExpect(jsonPath("$.miembros.length()").value(1))
                .andExpect(jsonPath("$.miembros[0].rol").value("LIDER"));

        mockMvc.perform(get("/api/proyectos/{id}/miembros", projectId)
                        .header("Authorization", bearer(outsider.token())))
                .andExpect(status().isNotFound());

        mockMvc.perform(patch(
                        "/api/proyectos/{projectId}/miembros/{userId}/rol",
                        projectId,
                        owner.userId())
                        .header("Authorization", bearer(owner.token()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"rol\":\"MIEMBRO\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("El proyecto debe conservar al menos un lider"));

        mockMvc.perform(post("/api/proyectos/{id}/miembros", projectId)
                        .header("Authorization", bearer(owner.token()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "correo": "%s",
                                  "rol": "MIEMBRO"
                                }
                                """.formatted(member.email())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.usuarioId").value(member.userId()))
                .andExpect(jsonPath("$.rol").value("MIEMBRO"));

        mockMvc.perform(post("/api/proyectos/{id}/miembros", projectId)
                        .header("Authorization", bearer(owner.token()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"correo":"%s"}
                                """.formatted(member.email())))
                .andExpect(status().isConflict());

        mockMvc.perform(post("/api/proyectos/{id}/miembros", projectId)
                        .header("Authorization", bearer(member.token()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"correo":"%s"}
                                """.formatted(outsider.email())))
                .andExpect(status().isForbidden());

        mockMvc.perform(patch(
                        "/api/proyectos/{projectId}/miembros/{userId}/rol",
                        projectId,
                        member.userId())
                        .header("Authorization", bearer(owner.token()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"rol\":\"LIDER\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rol").value("LIDER"));

        long taskId = createTask(owner.token(), projectId);

        mockMvc.perform(post("/api/tareas/{id}/comentarios", taskId)
                        .header("Authorization", bearer(member.token()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"contenido\":\"  Primer comentario  \"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.autorId").value(member.userId()))
                .andExpect(jsonPath("$.contenido").value("Primer comentario"))
                .andExpect(jsonPath("$.puedeEliminar").value(true));

        mockMvc.perform(post("/api/tareas/{id}/comentarios", taskId)
                        .header("Authorization", bearer(member.token()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"contenido\":\"   \"}"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get("/api/tareas/{id}/comentarios", taskId)
                        .header("Authorization", bearer(owner.token())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].puedeEliminar").value(true));

        mockMvc.perform(get("/api/tareas/{id}/comentarios", taskId)
                        .header("Authorization", bearer(outsider.token())))
                .andExpect(status().isNotFound());

        JsonNode ownerComment = objectMapper.readTree(mockMvc.perform(
                        post("/api/tareas/{id}/comentarios", taskId)
                                .header("Authorization", bearer(owner.token()))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"contenido\":\"Comentario del lider\"}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString());

        mockMvc.perform(delete("/api/comentarios/{id}",
                        ownerComment.get("id").asLong())
                        .header("Authorization", bearer(member.token())))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/proyectos/{id}/miembros", projectId)
                        .header("Authorization", bearer(member.token())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.puedeGestionar").value(true));

        mockMvc.perform(get("/api/proyectos/{id}/actividad", projectId)
                        .header("Authorization", bearer(member.token())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].tipo").value("COMENTARIO_CREADO"))
                .andExpect(jsonPath("$[?(@.tipo == 'MIEMBRO_AGREGADO')]")
                        .isNotEmpty())
                .andExpect(jsonPath("$[?(@.tipo == 'TAREA_CREADA')]")
                        .isNotEmpty())
                .andExpect(jsonPath("$[?(@.tipo == 'PROYECTO_CREADO')]")
                        .isNotEmpty());

        mockMvc.perform(delete(
                        "/api/proyectos/{projectId}/miembros/{userId}",
                        projectId,
                        member.userId())
                        .header("Authorization", bearer(owner.token())))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/proyectos/{id}/actividad", projectId)
                        .header("Authorization", bearer(member.token())))
                .andExpect(status().isNotFound());
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

    private long createProject(String token) throws Exception {
        JsonNode workspace = objectMapper.readTree(mockMvc.perform(
                        post("/api/espacios")
                                .header("Authorization", bearer(token))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"nombre\":\"Espacio Colaboracion\"}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString());
        JsonNode project = objectMapper.readTree(mockMvc.perform(
                        post("/api/proyectos")
                                .header("Authorization", bearer(token))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "nombre": "Proyecto Colaboracion",
                                          "espacioId": %d
                                        }
                                        """.formatted(workspace.get("id").asLong())))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString());
        return project.get("id").asLong();
    }

    private long createTask(String token, long projectId) throws Exception {
        JsonNode task = objectMapper.readTree(mockMvc.perform(
                        post("/api/tareas")
                                .header("Authorization", bearer(token))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "titulo": "Tarea colaborativa",
                                          "proyectoId": %d
                                        }
                                        """.formatted(projectId)))
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
