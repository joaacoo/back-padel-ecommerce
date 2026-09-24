package com.uade.e_commerce.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RestController;

import com.uade.e_commerce.dto.ActualizarCantidadRequest;
import com.uade.e_commerce.dto.AgregarItemRequest;
import com.uade.e_commerce.dto.ItemCarritoResponse;
import com.uade.e_commerce.model.ItemCarrito;
import com.uade.e_commerce.service.CarritoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/carrito")
@RequiredArgsConstructor
public class CarritoController {
    private final CarritoService carritoService;

    @GetMapping
    @Operation(summary = "Consultar carrito", description = "Obtiene los items del carrito del usuario. Si el usuario existe y no tiene carrito, crea uno vacío.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Items del carrito obtenidos; la lista puede estar vacía"),
            @ApiResponse(responseCode = "400", description = "El parámetro usuarioId falta o no es un número válido",
                    content = @Content(mediaType = "application/json", schema = @Schema(type = "object"))),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(type = "object")))
    })
    public ResponseEntity<List<ItemCarritoResponse>> verCarrito(@RequestParam Long usuarioId) {
        List<ItemCarritoResponse> items = carritoService.getAllItemCarritos(usuarioId).stream()
                .map(this::aResponse)
                .toList();
        return ResponseEntity.ok(items);
    }

    @PostMapping("/productos")
    @Operation(summary = "Agregar producto al carrito", description = "Agrega un producto al carrito del usuario o suma la cantidad al item existente, verificando el stock disponible.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Item agregado o cantidad acumulada en el item existente"),
            @ApiResponse(responseCode = "400", description = "usuarioId ausente o inválido, cuerpo ausente o inválido, datos que no cumplen la validación o stock insuficiente",
                    content = @Content(mediaType = "application/json", schema = @Schema(type = "object"))),
            @ApiResponse(responseCode = "404", description = "Usuario o producto no encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(type = "object")))
    })
    public ResponseEntity<ItemCarritoResponse> agregarItem(@RequestParam Long usuarioId,
            @Valid @RequestBody AgregarItemRequest request) {
        ItemCarrito item = carritoService.addItemCarrito(
                usuarioId, request.getProductoId(),
                request.getCantidad(), request.getPrecioUnitario());
        return ResponseEntity.status(HttpStatus.CREATED).body(aResponse(item));
    }

    @PutMapping("/items/{itemId}")
    @Operation(summary = "Actualizar cantidad de un item", description = "Reemplaza la cantidad de un item del carrito, verificando que sea positiva y no supere el stock disponible.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cantidad del item actualizada"),
            @ApiResponse(responseCode = "400", description = "itemId inválido, cuerpo ausente o inválido, cantidad que no cumple la validación o stock insuficiente",
                    content = @Content(mediaType = "application/json", schema = @Schema(type = "object"))),
            @ApiResponse(responseCode = "404", description = "Item no encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(type = "object")))
    })
    public ResponseEntity<ItemCarritoResponse> actualizar(@PathVariable Long itemId,
            @Valid @RequestBody ActualizarCantidadRequest request) {
        ItemCarrito actualizado = carritoService.actualizarCantidad(itemId, request.getCantidad());
        return ResponseEntity.ok(aResponse(actualizado));
    }

    @DeleteMapping("/productos/{productoId}")
    @Operation(summary = "Eliminar producto del carrito", description = "Elimina el item del producto indicado del carrito del usuario. Si el producto no está en el carrito, finaliza igualmente sin contenido.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Operación completada, incluso si el producto no estaba en el carrito", content = @Content),
            @ApiResponse(responseCode = "400", description = "usuarioId ausente o identificadores que no son números válidos",
                    content = @Content(mediaType = "application/json", schema = @Schema(type = "object"))),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(type = "object")))
    })
    public ResponseEntity<Void> eliminar(@RequestParam Long usuarioId, @PathVariable Long productoId) {
        carritoService.eliminarItem(usuarioId, productoId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping()
    @Operation(summary = "Vaciar carrito", description = "Elimina todos los items del carrito del usuario. Si el usuario existe y no tiene carrito, crea uno vacío.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Carrito vacío, sin contenido en la respuesta", content = @Content),
            @ApiResponse(responseCode = "400", description = "El parámetro usuarioId falta o no es un número válido",
                    content = @Content(mediaType = "application/json", schema = @Schema(type = "object"))),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(type = "object")))
    })
    public ResponseEntity<Void> vaciar(@RequestParam Long usuarioId) {
        carritoService.vaciarCarrito(usuarioId);
        return ResponseEntity.noContent().build();
    }

    private ItemCarritoResponse aResponse(ItemCarrito item) {
        Long productoId = null;
        String productoNombre = null;
        if (item.getProducto() != null) {
            productoId = item.getProducto().getId();
            productoNombre = item.getProducto().getNombre();
        }
        return new ItemCarritoResponse(
                item.getId(),
                productoId,
                productoNombre,
                item.getCantidad(),
                item.getPrecioUnitario());
    }
}
