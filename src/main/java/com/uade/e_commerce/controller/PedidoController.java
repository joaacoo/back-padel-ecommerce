package com.uade.e_commerce.controller;

import com.uade.e_commerce.dto.CrearPedidoRequest;
import com.uade.e_commerce.dto.PedidoResponse;
import com.uade.e_commerce.model.Pedido;
import com.uade.e_commerce.service.PedidoService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    // POST /api/pedidos
    @PostMapping
    public ResponseEntity<PedidoResponse> crearPedido(
            @Valid @RequestBody CrearPedidoRequest datos) {

        Pedido pedido = pedidoService.crearPedido(datos.usuarioId());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(aResponse(pedido));
    }

    // GET /api/pedidos/{id}
    @GetMapping("/{id}")
    public ResponseEntity<PedidoResponse> obtenerPorId(@PathVariable Long id) {

        return ResponseEntity.ok(
                aResponse(pedidoService.obtenerPorId(id))
        );
    }

    // GET /api/pedidos/usuario/{id}
    @GetMapping("/usuario/{id}")
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
