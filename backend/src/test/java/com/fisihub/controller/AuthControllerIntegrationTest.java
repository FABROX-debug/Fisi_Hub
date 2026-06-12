package com.fisihub.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fisihub.model.RolNombre;
import com.fisihub.model.Usuario;
import com.fisihub.repository.RolRepository;
import com.fisihub.repository.UsuarioRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void cleanUsers() {
        usuarioRepository.deleteAll();
    }

    @Test
    void initializesSystemRoles() {
        Set<RolNombre> roleNames = rolRepository.findAll().stream()
                .map(rol -> rol.getNombre())
                .collect(Collectors.toSet());

        assertThat(roleNames).containsExactlyInAnyOrder(
                Arrays.stream(RolNombre.values()).toArray(RolNombre[]::new));
    }

    @Test
    void registerLoginAndMeUseJwtAndBcrypt() throws Exception {
        String registerBody = """
                {
                  "nombre": "Fabrizio Test",
                  "correo": "fabrizio.test@fisihub.local",
                  "password": "Test1234"
                }
                """;

        MvcResult registerResult = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.usuario.nombre")
                        .value("Fabrizio Test"))
                .andExpect(jsonPath("$.usuario.roles[0]").value("MIEMBRO"))
                .andReturn();

        Usuario stored = usuarioRepository
                .findByCorreoIgnoreCase("fabrizio.test@fisihub.local")
                .orElseThrow();
        assertThat(stored.getPassword()).isNotEqualTo("Test1234");
        assertThat(stored.getPassword()).startsWith("$2");
        assertThat(passwordEncoder.matches("Test1234", stored.getPassword()))
                .isTrue();

        JsonNode registerJson = objectMapper.readTree(
                registerResult.getResponse().getContentAsString());
        String token = registerJson.get("token").asText();

        mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.correo")
                        .value("fabrizio.test@fisihub.local"))
                .andExpect(jsonPath("$.roles[0]").value("MIEMBRO"));

        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "correo": "fabrizio.test@fisihub.local",
                                  "password": "Test1234"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.usuario.nombre")
                        .value("Fabrizio Test"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "correo": "fabrizio.test@fisihub.local",
                                  "password": "Incorrecta123"
                                }
                                """))
                .andExpect(status().isUnauthorized());
    }
}
