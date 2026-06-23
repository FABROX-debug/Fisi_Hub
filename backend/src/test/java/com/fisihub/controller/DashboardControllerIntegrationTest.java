package com.fisihub.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fisihub.repository.EspacioMiembroRepository;
import com.fisihub.repository.EspacioTrabajoRepository;
import com.fisihub.repository.MiembroProyectoRepository;
import com.fisihub.repository.NotificacionRepository;
import com.fisihub.repository.ProyectoRepository;
import com.fisihub.repository.TareaRepository;
import com.fisihub.repository.UsuarioRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class DashboardControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private TareaRepository tareaRepository;

    @Autowired
    private NotificacionRepository notificacionRepository;

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
        notificacionRepository.deleteAll();
        tareaRepository.deleteAll();
        miembroProyectoRepository.deleteAll();
        proyectoRepository.deleteAll();
        espacioMiembroRepository.deleteAll();
        espacioRepository.deleteAll();
        usuarioRepository.deleteAll();
    }

    @Test
    void requiresJwtForDashboard() throws Exception {
        mockMvc.perform(get("/api/dashboard/resumen"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void returnsOnlyAuthenticatedUserDashboardData() throws Exception {
        AuthData owner = register(
                "Dashboard Owner",
                "dashboard.owner@fisihub.local");
        AuthData outsider = register(
                "Dashboard Outsider",
                "dashboard.outsider@fisihub.local");

        long activeProjectId = createProject(
                owner.token(),
                "Proyecto Activo",
                "EN_PROCESO");
        createProject(owner.token(), "Proyecto Finalizado", "FINALIZADO");
        long outsiderProjectId = createProject(
                outsider.token(),
                "Proyecto Ajeno",
                "EN_PROCESO");

        createTask(
                owner.token(),
                activeProjectId,
                "Tarea para hoy",
                LocalDate.now(),
                "PENDIENTE");
        createTask(
                owner.token(),
                activeProjectId,
                "Tarea proxima",
                LocalDate.now().plusDays(2),
                "EN_PROCESO");
        createTask(
                owner.token(),
                activeProjectId,
                "Tarea completada",
                LocalDate.now().plusDays(5),
                "COMPLETADA");
        long overdueTaskId = createTask(
                owner.token(),
                activeProjectId,
                "Tarea vencida",
                LocalDate.now().plusDays(1),
                "BLOQUEADA");
        jdbcTemplate.update(
                "update tarea set fecha_limite = ? where id = ?",
                LocalDate.now().minusDays(2),
                overdueTaskId);

        createTask(
                outsider.token(),
                outsiderProjectId,
                "Tarea ajena",
                LocalDate.now(),
                "PENDIENTE");

        mockMvc.perform(get("/api/dashboard/resumen")
                        .header("Authorization", bearer(owner.token())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalProyectosActivos").value(1))
                .andExpect(jsonPath("$.tareasPendientes").value(3))
                .andExpect(jsonPath("$.tareasCompletadas").value(1))
                .andExpect(jsonPath("$.tareasVencidas").value(1))
                .andExpect(jsonPath("$.tareasParaHoy").value(1))
                .andExpect(jsonPath("$.porcentajePromedioAvance").value(13))
                .andExpect(jsonPath("$.proyectosActivosRecientes.length()")
                        .value(1))
                .andExpect(jsonPath("$.proyectosActivosRecientes[0].nombre")
                        .value("Proyecto Activo"))
                .andExpect(jsonPath("$.tareasProximas.length()").value(2))
                .andExpect(jsonPath("$.tareasVencidasDetalle.length()")
                        .value(1))
                .andExpect(jsonPath("$.tareasVencidasDetalle[0].titulo")
                        .value("Tarea vencida"))
                .andExpect(jsonPath("$.actividadReciente.length()").value(5))
                .andExpect(jsonPath("$.actividadReciente[0].tipo")
                        .value("TAREA_CREADA"));
    }

    private AuthData register(String nombre, String correo) throws Exception {
        JsonNode response = objectMapper.readTree(mockMvc.perform(
                        post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "nombre": "%s",
                                          "correo": "%s",
                                          "password": "Password123"
                                        }
                                        """.formatted(nombre, correo)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString());
        return new AuthData(response.get("token").asText());
    }

    private long createProject(
            String token,
            String nombre,
            String estado) throws Exception {
        JsonNode workspace = objectMapper.readTree(mockMvc.perform(
                        post("/api/espacios")
                                .header("Authorization", bearer(token))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {"nombre":"Espacio %s"}
                                        """.formatted(nombre)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString());
        JsonNode project = objectMapper.readTree(mockMvc.perform(
                        post("/api/proyectos")
                                .header("Authorization", bearer(token))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "nombre": "%s",
                                          "espacioId": %d,
                                          "estado": "%s",
                                          "prioridad": "ALTA"
                                        }
                                        """.formatted(
                                        nombre,
                                        workspace.get("id").asLong(),
                                        estado)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString());
        return project.get("id").asLong();
    }

    private long createTask(
            String token,
            long projectId,
            String titulo,
            LocalDate fechaLimite,
            String estado) throws Exception {
        JsonNode task = objectMapper.readTree(mockMvc.perform(
                        post("/api/tareas")
                                .header("Authorization", bearer(token))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "titulo": "%s",
                                          "proyectoId": %d,
                                          "fechaLimite": "%s",
                                          "estado": "%s",
                                          "prioridad": "MEDIA"
                                        }
                                        """.formatted(
                                        titulo,
                                        projectId,
                                        fechaLimite,
                                        estado)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString());
        return task.get("id").asLong();
    }

    private String bearer(String token) {
        return "Bearer " + token;
    }

    private record AuthData(String token) {
    }
}
