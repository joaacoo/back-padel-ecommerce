package com.uade.e_commerce.controller;

import com.uade.e_commerce.dto.CrearPedidoRequest;
import com.uade.e_commerce.dto.PedidoResponse;
import com.uade.e_commerce.model.Pedido;
import com.uade.e_commerce.service.PedidoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @Operation(summary = "Crear pedido", description = "Crea un pedido a partir del carrito del usuario, calcula el total, descuenta el stock y vacía el carrito.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Pedido creado"),
            @ApiResponse(responseCode = "400", description = "Cuerpo ausente o inválido, usuarioId ausente, carrito vacío o stock insuficiente",
                    content = @Content(mediaType = "application/json", schema = @Schema(type = "object"))),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(type = "object")))
    })
    public ResponseEntity<PedidoResponse> crearPedido(
            @Valid @RequestBody CrearPedidoRequest datos) {

        Pedido pedido = pedidoService.crearPedido(datos.getUsuarioId());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(aResponse(pedido));
    }

    // GET /api/pedidos/{id}
    @GetMapping("/{id}")
    @Operation(summary = "Consultar pedido", description = "Obtiene el identificador, la fecha y el total de un pedido existente.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pedido encontrado"),
            @ApiResponse(responseCode = "400", description = "El identificador del pedido no es un número válido",
                    content = @Content(mediaType = "application/json", schema = @Schema(type = "object"))),
            @ApiResponse(responseCode = "404", description = "Pedido no encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(type = "object")))
    })
    public ResponseEntity<PedidoResponse> obtenerPorId(@PathVariable Long id) {

        return ResponseEntity.ok(
                aResponse(pedidoService.obtenerPorId(id))
        );
    }

    // GET /api/pedidos/usuario/{id}
    @GetMapping("/usuario/{id}")
    @Operation(summary = "Consultar pedidos de un usuario", description = "Lista los pedidos de un usuario existente; devuelve una lista vacía si todavía no tiene pedidos.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pedidos del usuario obtenidos; la lista puede estar vacía"),
            @ApiResponse(responseCode = "400", description = "El identificador del usuario no es un número válido",
                    content = @Content(mediaType = "application/json", schema = @Schema(type = "object"))),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(type = "object")))
    })
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
