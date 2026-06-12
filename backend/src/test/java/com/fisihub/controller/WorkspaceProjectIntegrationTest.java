package com.fisihub.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
import com.fisihub.model.RolEspacio;
import com.fisihub.model.RolProyecto;
import com.fisihub.repository.EspacioMiembroRepository;
import com.fisihub.repository.EspacioTrabajoRepository;
import com.fisihub.repository.MiembroProyectoRepository;
import com.fisihub.repository.ProyectoRepository;
import com.fisihub.repository.UsuarioRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class WorkspaceProjectIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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
        miembroProyectoRepository.deleteAll();
        proyectoRepository.deleteAll();
        espacioMiembroRepository.deleteAll();
        espacioRepository.deleteAll();
        usuarioRepository.deleteAll();
    }

    @Test
    void requiresJwtForWorkspaceAndProjectEndpoints() throws Exception {
        mockMvc.perform(get("/api/espacios"))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/proyectos"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void authenticatedUserCanManageOwnWorkspaceAndProjects() throws Exception {
        String token = registerAndGetToken(
                "Fabrizio Sprint 3",
                "fabrizio.sprint3@fisihub.local");

        JsonNode espacio = objectMapper.readTree(mockMvc.perform(
                        post("/api/espacios")
                                .header("Authorization", bearer(token))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "nombre": "Arquitectura de Software",
                                          "descripcion": "Espacio academico",
                                          "color": "#6D28D9",
                                          "icono": "folder"
                                        }
                                        """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre")
                        .value("Arquitectura de Software"))
                .andExpect(jsonPath("$.cantidadMiembros").value(1))
                .andExpect(jsonPath("$.cantidadProyectos").value(0))
                .andReturn().getResponse().getContentAsString());
        long espacioId = espacio.get("id").asLong();

        assertThat(espacioMiembroRepository
                .findByEspacioIdAndUsuarioCorreoIgnoreCase(
                        espacioId,
                        "fabrizio.sprint3@fisihub.local")
                .orElseThrow()
                .getRol())
                .isEqualTo(RolEspacio.LIDER);

        mockMvc.perform(get("/api/espacios")
                        .header("Authorization", bearer(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(espacioId));

        mockMvc.perform(put("/api/espacios/{id}", espacioId)
                        .header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nombre": "Arquitectura Actualizada",
                                  "descripcion": "Descripcion actualizada",
                                  "color": "#8B5CF6",
                                  "icono": "layers"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre")
                        .value("Arquitectura Actualizada"));

        JsonNode proyecto = objectMapper.readTree(mockMvc.perform(
                        post("/api/proyectos")
                                .header("Authorization", bearer(token))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "nombre": "Sistema FISIHUB",
                                          "descripcion": "MVP del sistema",
                                          "espacioId": %d,
                                          "fechaInicio": "2026-06-12",
                                          "fechaFin": "2026-08-30",
                                          "estado": "EN_PROCESO",
                                          "prioridad": "ALTA"
                                        }
                                        """.formatted(espacioId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.espacioId").value(espacioId))
                .andExpect(jsonPath("$.porcentajeAvance").value(0))
                .andExpect(jsonPath("$.estado").value("EN_PROCESO"))
                .andExpect(jsonPath("$.prioridad").value("ALTA"))
                .andExpect(jsonPath("$.cantidadMiembros").value(1))
                .andReturn().getResponse().getContentAsString());
        long proyectoId = proyecto.get("id").asLong();

        assertThat(miembroProyectoRepository
                .findByProyectoIdAndUsuarioCorreoIgnoreCase(
                        proyectoId,
                        "fabrizio.sprint3@fisihub.local")
                .orElseThrow()
                .getRol())
                .isEqualTo(RolProyecto.LIDER);

        mockMvc.perform(get("/api/proyectos")
                        .header("Authorization", bearer(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(proyectoId));

        mockMvc.perform(get("/api/espacios/{id}/proyectos", espacioId)
                        .header("Authorization", bearer(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(proyectoId));

        mockMvc.perform(put("/api/proyectos/{id}", proyectoId)
                        .header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nombre": "Sistema FISIHUB Actualizado",
                                  "descripcion": "MVP actualizado",
                                  "espacioId": %d,
                                  "fechaInicio": "2026-06-12",
                                  "fechaFin": "2026-09-15",
                                  "estado": "EN_REVISION",
                                  "prioridad": "URGENTE"
                                }
                                """.formatted(espacioId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("EN_REVISION"))
                .andExpect(jsonPath("$.prioridad").value("URGENTE"));

        mockMvc.perform(delete("/api/proyectos/{id}", proyectoId)
                        .header("Authorization", bearer(token)))
                .andExpect(status().isNoContent());
        assertThat(proyectoRepository.existsById(proyectoId)).isFalse();

        mockMvc.perform(delete("/api/espacios/{id}", espacioId)
                        .header("Authorization", bearer(token)))
                .andExpect(status().isNoContent());
        assertThat(espacioRepository.existsById(espacioId)).isFalse();
    }

    @Test
    void userCannotSeeResourcesOwnedByAnotherUser() throws Exception {
        String ownerToken = registerAndGetToken(
                "Usuario Propietario",
                "owner@fisihub.local");
        String otherToken = registerAndGetToken(
                "Usuario Externo",
                "other@fisihub.local");

        JsonNode espacio = objectMapper.readTree(mockMvc.perform(
                        post("/api/espacios")
                                .header("Authorization", bearer(ownerToken))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {"nombre":"Privado","color":"#6D28D9"}
                                        """))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString());
        long espacioId = espacio.get("id").asLong();

        JsonNode proyecto = objectMapper.readTree(mockMvc.perform(
                        post("/api/proyectos")
                                .header("Authorization", bearer(ownerToken))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "nombre":"Proyecto privado",
                                          "espacioId":%d
                                        }
                                        """.formatted(espacioId)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString());
        long proyectoId = proyecto.get("id").asLong();

        mockMvc.perform(get("/api/espacios")
                        .header("Authorization", bearer(otherToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
        mockMvc.perform(get("/api/espacios/{id}", espacioId)
                        .header("Authorization", bearer(otherToken)))
                .andExpect(status().isNotFound());
        mockMvc.perform(get("/api/proyectos")
                        .header("Authorization", bearer(otherToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
        mockMvc.perform(get("/api/proyectos/{id}", proyectoId)
                        .header("Authorization", bearer(otherToken)))
                .andExpect(status().isNotFound());
    }

    private String registerAndGetToken(String nombre, String correo)
            throws Exception {
        String response = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nombre": "%s",
                                  "correo": "%s",
                                  "password": "Password123"
                                }
                                """.formatted(nombre, correo)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(response).get("token").asText();
    }

    private String bearer(String token) {
        return "Bearer " + token;
    }
}
