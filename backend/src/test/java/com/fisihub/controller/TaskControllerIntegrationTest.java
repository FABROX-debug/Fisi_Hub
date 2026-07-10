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
import com.fisihub.model.PrioridadTarea;
import com.fisihub.model.Tarea;
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
class TaskControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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
    void requiresJwtForTaskEndpoints() throws Exception {
        mockMvc.perform(get("/api/tareas"))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/tareas/1/detalle"))
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
        mockMvc.perform(get("/api/proyectos/{id}/detalle", projectId)
                        .header("Authorization", bearer(owner.token())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.proyecto.id").value(projectId))
                .andExpect(jsonPath("$.resumenTareas.total").value(2))
                .andExpect(jsonPath("$.resumenTareas.pendientes").value(2))
                .andExpect(jsonPath("$.miembros.miembros.length()").value(1))
                .andExpect(jsonPath("$.tareasDestacadas.length()").value(2));
        mockMvc.perform(get("/api/tareas/{id}/detalle", firstTaskId)
                        .header("Authorization", bearer(owner.token())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tarea.id").value(firstTaskId))
                .andExpect(jsonPath("$.proyecto.id").value(projectId))
                .andExpect(jsonPath("$.alertas.sinResponsable").value(false))
                .andExpect(jsonPath("$.alertas.requiereAtencion").value(false));
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
        mockMvc.perform(get("/api/tareas/mi-trabajo")
                        .header("Authorization", bearer(owner.token())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resumen.pendientes").value(0))
                .andExpect(jsonPath("$.resumen.completadas").value(2))
                .andExpect(jsonPath("$.tareasAsignadas.length()").value(2))
                .andExpect(jsonPath("$.tareasPrioritarias").isEmpty())
                .andExpect(jsonPath("$.proyectosConCarga.length()").value(1))
                .andExpect(jsonPath("$.proyectosConCarga[0].id").value(projectId));
        mockMvc.perform(get("/api/tareas/{id}", firstTaskId)
                        .header("Authorization", bearer(outsider.token())))
                .andExpect(status().isNotFound());
        mockMvc.perform(get("/api/tareas/{id}/detalle", firstTaskId)
                        .header("Authorization", bearer(outsider.token())))
                .andExpect(status().isNotFound());
        mockMvc.perform(get("/api/proyectos/{id}/detalle", projectId)
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

    @Test
    void returnsPersonalWorkSummaryOrderedByUrgency() throws Exception {
        AuthData owner = register(
                "Responsable Flujo",
                "task.personal@fisihub.local");
        long projectId = createProject(owner.token());
        String todayDate = LocalDate.now().toString();
        String soonDate = LocalDate.now().plusDays(2).toString();
        String laterDate = LocalDate.now().plusDays(10).toString();

        JsonNode overdueTask = objectMapper.readTree(mockMvc.perform(post("/api/tareas")
                        .header("Authorization", bearer(owner.token()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "titulo": "Tarea atrasada",
                                  "proyectoId": %d,
                                  "responsableId": %d,
                                  "fechaLimite": "%s",
                                  "prioridad": "MEDIA"
                                }
                                """.formatted(projectId, owner.userId(), laterDate)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString());

        mockMvc.perform(post("/api/tareas")
                        .header("Authorization", bearer(owner.token()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "titulo": "Tarea urgente",
                                  "proyectoId": %d,
                                  "responsableId": %d,
                                  "fechaLimite": "%s",
                                  "prioridad": "URGENTE"
                                }
                                """.formatted(projectId, owner.userId(), laterDate)))
                .andExpect(status().isCreated());

        JsonNode blockedTask = objectMapper.readTree(mockMvc.perform(
                        post("/api/tareas")
                                .header("Authorization", bearer(owner.token()))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "titulo": "Tarea bloqueada",
                                          "proyectoId": %d,
                                          "responsableId": %d,
                                          "fechaLimite": "%s",
                                          "estado": "BLOQUEADA",
                                          "prioridad": "ALTA"
                                        }
                                        """.formatted(projectId, owner.userId(), soonDate)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString());

        mockMvc.perform(post("/api/tareas")
                        .header("Authorization", bearer(owner.token()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "titulo": "Tarea para hoy",
                                  "proyectoId": %d,
                                  "responsableId": %d,
                                  "fechaLimite": "%s"
                                }
                                """.formatted(projectId, owner.userId(), todayDate)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/tareas")
                        .header("Authorization", bearer(owner.token()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "titulo": "Tarea normal",
                                  "proyectoId": %d,
                                  "responsableId": %d,
                                  "fechaLimite": "%s",
                                  "estado": "EN_PROCESO"
                                }
                                """.formatted(projectId, owner.userId(), laterDate)))
                .andExpect(status().isCreated());

        Tarea overdueEntity = tareaRepository.findById(overdueTask.get("id").asLong())
                .orElseThrow();
        overdueEntity.actualizar(
                overdueEntity.getTitulo(),
                overdueEntity.getDescripcion(),
                overdueEntity.getResponsable(),
                LocalDate.now().minusDays(3),
                overdueEntity.getEstado(),
                PrioridadTarea.MEDIA);
        tareaRepository.saveAndFlush(overdueEntity);

        mockMvc.perform(get("/api/tareas/mi-trabajo")
                        .header("Authorization", bearer(owner.token())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resumen.pendientes").value(3))
                .andExpect(jsonPath("$.resumen.enProceso").value(1))
                .andExpect(jsonPath("$.resumen.bloqueadas").value(1))
                .andExpect(jsonPath("$.resumen.vencidas").value(1))
                .andExpect(jsonPath("$.resumen.paraHoy").value(1))
                .andExpect(jsonPath("$.tareasAsignadas.length()").value(5))
                .andExpect(jsonPath("$.tareasNecesitanAccion.length()").value(3))
                .andExpect(jsonPath("$.tareasPrioritarias[0].titulo").value("Tarea atrasada"))
                .andExpect(jsonPath("$.tareasPrioritarias[1].titulo").value("Tarea urgente"))
                .andExpect(jsonPath("$.tareasPrioritarias[2].titulo").value("Tarea bloqueada"))
                .andExpect(jsonPath("$.proyectosConCarga[0].tareasActivas").value(5));
        mockMvc.perform(get("/api/tareas/{id}/detalle", overdueTask.get("id").asLong())
                        .header("Authorization", bearer(owner.token())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.alertas.vencida").value(true))
                .andExpect(jsonPath("$.alertas.sinResponsable").value(false))
                .andExpect(jsonPath("$.alertas.requiereAtencion").value(true));

        long blockedTaskId = blockedTask.get("id").asLong();
        mockMvc.perform(patch("/api/tareas/{id}/estado", blockedTaskId)
                        .header("Authorization", bearer(owner.token()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"estado\":\"COMPLETADA\"}"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/tareas/mi-trabajo")
                        .header("Authorization", bearer(owner.token())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resumen.completadas").value(1))
                .andExpect(jsonPath("$.resumen.bloqueadas").value(0))
                .andExpect(jsonPath("$.tareasNecesitanAccion.length()").value(2))
                .andExpect(jsonPath("$.proyectosConCarga[0].tareasActivas").value(4));
        mockMvc.perform(get("/api/tareas/{id}/detalle", blockedTaskId)
                        .header("Authorization", bearer(owner.token())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.alertas.bloqueada").value(false));
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
