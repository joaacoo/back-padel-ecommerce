package com.uade.e_commerce;

import com.uade.e_commerce.model.Role;
import com.uade.e_commerce.model.Usuario;
import com.uade.e_commerce.repository.UsuarioRepository;
import com.uade.e_commerce.security.JwtService;
import jakarta.servlet.Filter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class SecurityIntegrationTests {
    @Autowired WebApplicationContext context;
    @Autowired @Qualifier("springSecurityFilterChain") Filter security;
    @Autowired UsuarioRepository users;
    @Autowired JwtService jwt;
    @Autowired PasswordEncoder encoder;
    MockMvc mvc;
    Usuario user;
    Usuario admin;

    @BeforeEach
    void setup() {
        mvc = MockMvcBuilders.webAppContextSetup(context).addFilters(security).build();
        user = users.findByEmailIgnoreCase("user@test.com").orElseGet(() -> users.save(new Usuario(
                "User", "Test", "user", "user@test.com", encoder.encode("password"), null, null, Role.USER)));
        admin = users.findByEmailIgnoreCase("admin@test.com").orElseGet(() -> users.save(new Usuario(
                "Admin", "Test", "admin", "admin@test.com", encoder.encode("password"), null, null, Role.ADMIN)));
    }

    @Test
    void publicCatalogAndProtectedResources() throws Exception {
        mvc.perform(get("/api/productos")).andExpect(status().isOk());
        mvc.perform(get("/api/usuarios/" + user.getId())).andExpect(status().isUnauthorized());
        mvc.perform(get("/api/usuarios/" + user.getId()).header("Authorization", "Bearer " + jwt.generateToken(user)))
                .andExpect(status().isOk()).andExpect(jsonPath("email").value("user@test.com"));
        mvc.perform(get("/api/usuarios/" + user.getId())).andExpect(status().isUnauthorized());
    }

    @Test
    void invalidTokensAreUnauthorized() throws Exception {
        mvc.perform(get("/api/usuarios/1").header("Authorization", "Bearer invalid"))
                .andExpect(status().isUnauthorized());
        String expired = io.jsonwebtoken.Jwts.builder().setSubject(user.getUsername())
                .setExpiration(new java.util.Date(0))
                .signWith(io.jsonwebtoken.security.Keys.hmacShaKeyFor(
                        "test-secret-with-at-least-thirty-two-bytes-for-hs256".getBytes()))
                .compact();
        mvc.perform(get("/api/usuarios/1").header("Authorization", "Bearer " + expired))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void onlyAdminCanCreateProducts() throws Exception {
        String product = "{\"nombre\":\"Paleta\",\"descripcion\":\"Control\",\"precio\":0.10,\"stock\":5,\"categoria\":\"Paletas\"}";
        mvc.perform(post("/api/productos").contentType("application/json").content(product))
                .andExpect(status().isUnauthorized());
        mvc.perform(post("/api/productos").header("Authorization", "Bearer " + jwt.generateToken(user))
                .contentType("application/json").content(product)).andExpect(status().isForbidden());
        mvc.perform(post("/api/productos").header("Authorization", "Bearer " + jwt.generateToken(admin))
                .contentType("application/json").content(product)).andExpect(status().isCreated());
    }

    @Test
    void loginReturnsJwt() throws Exception {
        mvc.perform(post("/api/auth/login").contentType("application/json")
                .content("{\"email\":\"user@test.com\",\"password\":\"password\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("token").isNotEmpty());
    }
}
