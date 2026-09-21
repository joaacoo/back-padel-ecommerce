package com.uade.e_commerce.controller;

import org.springframework.web.bind.annotation.RestController;

import com.uade.e_commerce.dto.ActualizarCantidadRequest;
import com.uade.e_commerce.dto.AgregarItemRequest;
import com.uade.e_commerce.dto.ItemCarritoResponse;
import com.uade.e_commerce.model.ItemCarrito;
import com.uade.e_commerce.service.CarritoService;

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
    public ResponseEntity<List<ItemCarritoResponse>> verCarrito(@RequestParam Long usuarioId) {
        List<ItemCarritoResponse> items = carritoService.getAllItemCarritos(usuarioId).stream()
                .map(this::aResponse)
                .toList();
        return ResponseEntity.ok(items);
    }

    @PostMapping("/productos")
    public ResponseEntity<ItemCarritoResponse> agregarItem(@RequestParam Long usuarioId,
            @RequestBody AgregarItemRequest request) {
        ItemCarrito item = carritoService.addItemCarrito(
                usuarioId, request.getProductoId(),
                request.getCantidad(), request.getPrecioUnitario());
        return ResponseEntity.status(HttpStatus.CREATED).body(aResponse(item));
    }

    @PutMapping("/items/{itemId}")
    public ResponseEntity<ItemCarritoResponse> actualizar(@PathVariable Long itemId,
            @RequestBody ActualizarCantidadRequest request) {
        ItemCarrito actualizado = carritoService.actualizarCantidad(itemId, request.getCantidad());
        return ResponseEntity.ok(aResponse(actualizado));
    }

    @DeleteMapping("/productos/{productoId}")
    public ResponseEntity<Void> eliminar(@RequestParam Long usuarioId, @PathVariable Long productoId) {
        carritoService.eliminarItem(usuarioId, productoId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping()
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
                item.getPrecioUnitario()
        );
    }
}
