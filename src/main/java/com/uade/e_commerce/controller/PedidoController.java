package com.uade.e_commerce.controller;

import com.uade.e_commerce.dto.CrearPedidoRequest;
import com.uade.e_commerce.dto.PedidoResponse;
import com.uade.e_commerce.model.Pedido;
import com.uade.e_commerce.service.PedidoService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    // POST /api/pedidos
    @PostMapping("/api/pedidos")
    public ResponseEntity<PedidoResponse> crearPedido(
            @RequestBody CrearPedidoRequest datos) {

        Pedido pedido = pedidoService.crearPedido(datos.usuarioId());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(aResponse(pedido));
    }

    // GET /api/pedidos/{id}
    @GetMapping("/api/pedidos/{id}")
    public ResponseEntity<PedidoResponse> obtenerPorId(@PathVariable Long id) {

        return ResponseEntity.ok(
                aResponse(pedidoService.obtenerPorId(id))
        );
    }

    // GET /api/users/{id}/pedidos
    @GetMapping("/api/users/{id}/pedidos")
    public ResponseEntity<List<PedidoResponse>> obtenerPedidosDeUsuario(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                pedidoService.obtenerPedidosDeUsuario(id).stream()
                        .map(this::aResponse)
                        .toList()
        );
    }

    private PedidoResponse aResponse(Pedido pedido) {
        return new PedidoResponse(pedido.getId(), pedido.getFecha(), pedido.getTotal());
    }
}
