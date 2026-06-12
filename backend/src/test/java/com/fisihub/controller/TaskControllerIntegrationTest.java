package com.fisihub.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
import com.fisihub.repository.EspacioMiembroRepository;
import com.fisihub.repository.EspacioTrabajoRepository;
import com.fisihub.repository.MiembroProyectoRepository;
import com.fisihub.repository.ProyectoRepository;
import com.fisihub.repository.TareaRepository;
import com.fisihub.repository.UsuarioRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TaskControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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
        tareaRepository.deleteAll();
        miembroProyectoRepository.deleteAll();
        proyectoRepository.deleteAll();
        espacioMiembroRepository.deleteAll();
        espacioRepository.deleteAll();
        usuarioRepository.deleteAll();
    }

    @Test
    void requiresJwtForTaskEndpoints() throws Exception {
        mockMvc.perform(get("/api/tareas"))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(post("/api/tareas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void managesTasksAndRecalculatesProjectProgress() throws Exception {
        AuthData owner = register(
                "Lider Tareas",
                "task.owner@fisihub.local");
        AuthData outsider = register(
                "Usuario Externo",
                "task.outsider@fisihub.local");
        long projectId = createProject(owner.token());
        String dueDate = LocalDate.now().plusDays(10).toString();

        JsonNode firstTask = objectMapper.readTree(mockMvc.perform(
                        post("/api/tareas")
                                .header("Authorization", bearer(owner.token()))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "titulo": "Implementar servicio",
                                          "descripcion": "Crear logica de tareas",
                                          "proyectoId": %d,
                                          "responsableId": %d,
                                          "fechaLimite": "%s"
                                        }
                                        """.formatted(
                                        projectId,
                                        owner.userId(),
                                        dueDate)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.estado").value("PENDIENTE"))
                .andExpect(jsonPath("$.prioridad").value("MEDIA"))
                .andExpect(jsonPath("$.responsableId").value(owner.userId()))
                .andReturn().getResponse().getContentAsString());
        long firstTaskId = firstTask.get("id").asLong();

        JsonNode secondTask = objectMapper.readTree(mockMvc.perform(
                        post("/api/tareas")
                                .header("Authorization", bearer(owner.token()))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "titulo": "Probar endpoints",
                                          "proyectoId": %d,
                                          "prioridad": "ALTA"
                                        }
                                        """.formatted(projectId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.responsableId").doesNotExist())
                .andReturn().getResponse().getContentAsString());
        long secondTaskId = secondTask.get("id").asLong();

        mockMvc.perform(get("/api/tareas")
                        .header("Authorization", bearer(owner.token())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
        mockMvc.perform(get("/api/proyectos/{id}/tareas", projectId)
                        .header("Authorization", bearer(owner.token())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
        mockMvc.perform(get("/api/tareas")
                        .param("prioridad", "ALTA")
                        .param("proyectoId", String.valueOf(projectId))
                        .header("Authorization", bearer(owner.token())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(secondTaskId));

        mockMvc.perform(patch("/api/tareas/{id}/estado", firstTaskId)
                        .header("Authorization", bearer(owner.token()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"estado\":\"COMPLETADA\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("COMPLETADA"));
        assertProjectProgress(projectId, owner.token(), 50);

        mockMvc.perform(put("/api/tareas/{id}", secondTaskId)
                        .header("Authorization", bearer(owner.token()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "titulo": "Probar endpoints actualizados",
                                  "descripcion": "Cobertura completa",
                                  "responsableId": %d,
                                  "fechaLimite": "%s",
                                  "estado": "COMPLETADA",
                                  "prioridad": "URGENTE"
                                }
                                """.formatted(owner.userId(), dueDate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo")
                        .value("Probar endpoints actualizados"))
                .andExpect(jsonPath("$.estado").value("COMPLETADA"))
                .andExpect(jsonPath("$.prioridad").value("URGENTE"));
        assertProjectProgress(projectId, owner.token(), 100);

        mockMvc.perform(patch("/api/tareas/{id}/estado", firstTaskId)
                        .header("Authorization", bearer(owner.token()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"estado\":\"EN_PROCESO\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("EN_PROCESO"));
        assertProjectProgress(projectId, owner.token(), 50);

        mockMvc.perform(patch("/api/tareas/{id}/estado", firstTaskId)
                        .header("Authorization", bearer(owner.token()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"estado\":\"COMPLETADA\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("COMPLETADA"));
        assertProjectProgress(projectId, owner.token(), 100);

        mockMvc.perform(post("/api/tareas")
                        .header("Authorization", bearer(owner.token()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "titulo": "Responsable invalido",
                                  "proyectoId": %d,
                                  "responsableId": %d
                                }
                                """.formatted(projectId, outsider.userId())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("El responsable debe ser miembro del proyecto"));

        mockMvc.perform(post("/api/tareas")
                        .header("Authorization", bearer(owner.token()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "titulo": "Fecha invalida",
                                  "proyectoId": %d,
                                  "fechaLimite": "%s"
                                }
                                """.formatted(
                                projectId,
                                LocalDate.now().minusDays(1))))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get("/api/tareas")
                        .header("Authorization", bearer(outsider.token())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
        mockMvc.perform(get("/api/tareas/{id}", firstTaskId)
                        .header("Authorization", bearer(outsider.token())))
                .andExpect(status().isNotFound());

        mockMvc.perform(delete("/api/tareas/{id}", firstTaskId)
                        .header("Authorization", bearer(owner.token())))
                .andExpect(status().isNoContent());
        assertProjectProgress(projectId, owner.token(), 100);

        mockMvc.perform(delete("/api/tareas/{id}", secondTaskId)
                        .header("Authorization", bearer(owner.token())))
                .andExpect(status().isNoContent());
        assertProjectProgress(projectId, owner.token(), 0);
        assertThat(tareaRepository.count()).isZero();
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
                response.get("usuario").get("id").asLong());
    }

    private long createProject(String token) throws Exception {
        JsonNode workspace = objectMapper.readTree(mockMvc.perform(
                        post("/api/espacios")
                                .header("Authorization", bearer(token))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"nombre\":\"Espacio Tareas\"}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString());
        JsonNode project = objectMapper.readTree(mockMvc.perform(
                        post("/api/proyectos")
                                .header("Authorization", bearer(token))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "nombre": "Proyecto Tareas",
                                          "espacioId": %d
                                        }
                                        """.formatted(workspace.get("id").asLong())))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString());
        return project.get("id").asLong();
    }

    private void assertProjectProgress(
            long projectId,
            String token,
            int expected) throws Exception {
        mockMvc.perform(get("/api/proyectos/{id}", projectId)
                        .header("Authorization", bearer(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.porcentajeAvance").value(expected));
    }

    private String bearer(String token) {
        return "Bearer " + token;
    }

    private record AuthData(String token, long userId) {
    }
}
