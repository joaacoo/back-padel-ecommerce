package com.uade.e_commerce.controller;

import com.uade.e_commerce.model.Usuario;
import com.uade.e_commerce.repository.UsuarioRepository;
import com.uade.e_commerce.service.PasswordService;

import tools.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfiguration
class UsuarioControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordService passwordService;

    @Test
    void registraUnUsuarioSinDevolverLaPassword() throws Exception {
        Map<String, String> datos = Map.of(
                "nombre", "Lucas",
                "email", "lucas@example.com",
                "password", "123456");

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(datos)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.nombre").value("Lucas"))
                .andExpect(jsonPath("$.email").value("lucas@example.com"))
                .andExpect(jsonPath("$.password").doesNotExist());

        Usuario usuarioGuardado = usuarioRepository.findByEmailIgnoreCase("lucas@example.com")
                .orElseThrow();
        org.junit.jupiter.api.Assertions.assertNotEquals("123456", usuarioGuardado.getPassword());
        org.junit.jupiter.api.Assertions.assertTrue(
                passwordService.coincide("123456", usuarioGuardado.getPassword()));
    }

    @Test
    void rechazaCamposVacios() throws Exception {
        Map<String, String> datos = Map.of(
                "nombre", "",
                "email", "otro@example.com",
                "password", "123456");

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(datos)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void rechazaUnEmailDuplicado() throws Exception {
        Map<String, String> datos = Map.of(
                "nombre", "Ana",
                "email", "ana@example.com",
                "password", "123456");
        String cuerpo = objectMapper.writeValueAsString(datos);

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cuerpo))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cuerpo))
                .andExpect(status().isConflict());
    }

    @Test
    void iniciaSesionSinDevolverLaPassword() throws Exception {
        Usuario guardado = usuarioRepository.save(new Usuario(
                "Maria", "maria@example.com", passwordService.hashear("secreto"), null, null));

        Map<String, String> datos = Map.of(
                "email", "MARIA@example.com",
                "password", "secreto");

        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(datos)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(guardado.getId()))
                .andExpect(jsonPath("$.nombre").value("Maria"))
                .andExpect(jsonPath("$.email").value("maria@example.com"))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    void rechazaCredencialesIncorrectas() throws Exception {
        usuarioRepository.save(new Usuario(
                "Pedro", "pedro@example.com", passwordService.hashear("correcta"), null, null));

        Map<String, String> datos = Map.of(
                "email", "pedro@example.com",
                "password", "incorrecta");

        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(datos)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void obtieneUnUsuarioPorIdSinDevolverLaPassword() throws Exception {
        Usuario guardado = usuarioRepository.save(new Usuario(
                "Sofia", "sofia@example.com", passwordService.hashear("123456"), null, null));

        mockMvc.perform(get("/api/users/{id}", guardado.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(guardado.getId()))
                .andExpect(jsonPath("$.nombre").value("Sofia"))
                .andExpect(jsonPath("$.email").value("sofia@example.com"))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    void respondeNotFoundSiElUsuarioNoExiste() throws Exception {
        mockMvc.perform(get("/api/users/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }
}
