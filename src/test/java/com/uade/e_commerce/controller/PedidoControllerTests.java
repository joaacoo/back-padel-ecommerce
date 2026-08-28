package com.uade.e_commerce.controller;

import com.uade.e_commerce.dto.RegistroUsuarioRequest;
import com.uade.e_commerce.model.Pedido;
import com.uade.e_commerce.model.Usuario;
import com.uade.e_commerce.repository.PedidoRepository;
import com.uade.e_commerce.repository.UsuarioRepository;
import com.uade.e_commerce.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PedidoControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private UsuarioService usuarioService;

    @BeforeEach
    void limpiarBase() {
        pedidoRepository.deleteAll();
        usuarioRepository.deleteAll();
    }

    @Test
    void obtienePedidoSinDevolverDatosDelUsuario() throws Exception {
        Usuario usuario = usuarioService.registrar(
                new RegistroUsuarioRequest("Ana", "ana@example.com", "secreto", null, null));
        Pedido pedido = new Pedido(null, LocalDateTime.now(), 150000.0, usuario);
        pedido = pedidoRepository.save(pedido);

        mockMvc.perform(get("/api/pedidos/{id}", pedido.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(pedido.getId()))
                .andExpect(jsonPath("$.fecha").exists())
                .andExpect(jsonPath("$.total").value(150000.0))
                .andExpect(jsonPath("$.usuario").doesNotExist())
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    void listaPedidosSinDevolverDatosDelUsuario() throws Exception {
        Usuario usuario = usuarioService.registrar(
                new RegistroUsuarioRequest("Juan", "juan@example.com", "secreto", null, null));
        pedidoRepository.save(new Pedido(null, LocalDateTime.now(), 90000.0, usuario));

        mockMvc.perform(get("/api/users/{id}/pedidos", usuario.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].usuario").doesNotExist())
                .andExpect(jsonPath("$[0].password").doesNotExist());
    }
}
