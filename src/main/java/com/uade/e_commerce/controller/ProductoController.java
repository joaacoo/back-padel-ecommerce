package com.uade.e_commerce.controller;

import jakarta.validation.Valid;
import com.uade.e_commerce.dto.ProductoRequest;
import com.uade.e_commerce.dto.ProductoResponse;
import com.uade.e_commerce.model.Producto;
import com.uade.e_commerce.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    // GET /api/productos obtener catalogo completo de padel
    @GetMapping
    public ResponseEntity<List<ProductoResponse>> obtenerTodos() {
        List<ProductoResponse> productos = productoService.obtenerTodos().stream()
                .map(this::aResponse)
                .toList();
        return ResponseEntity.ok(productos);
    }

    // GET /api/productos/{id} obtener detalle de una paleta/producto especifico
    @GetMapping("/{id}")
    public ResponseEntity<ProductoResponse> obtenerPorId(@PathVariable Long id) { // Captura el {id} de la URL
        Producto producto = productoService.obtenerPorId(id);
        if (producto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(aResponse(producto));
    }

    // POST /api/productos Cargar un nuevo producto de padel al catalogo
    @PostMapping
    public ResponseEntity<ProductoResponse> crearProducto(@Valid @RequestBody ProductoRequest request) {
        Producto producto = new Producto();
        producto.setNombre(request.getNombre());
        producto.setDescripcion(request.getDescripcion());
        producto.setPrecio(request.getPrecio());
        producto.setStock(request.getStock());
        producto.setCategoria(request.getCategoria());
        producto.setImagenUrl(request.getImagenUrl());
        
        Producto nuevoProducto = productoService.crearProducto(producto);
        return ResponseEntity.status(201).body(aResponse(nuevoProducto));
    }

    // PUT /api/productos/{id} modificar un producto existente
    @PutMapping("/{id}")
    public ResponseEntity<ProductoResponse> modificarProducto(@PathVariable Long id, @Valid @RequestBody ProductoRequest request) {
        Producto producto = new Producto();
        producto.setNombre(request.getNombre());
        producto.setDescripcion(request.getDescripcion());
        producto.setPrecio(request.getPrecio());
        producto.setStock(request.getStock());
        producto.setCategoria(request.getCategoria());
        producto.setImagenUrl(request.getImagenUrl());
        
        Producto actualizado = productoService.actualizarProducto(id, producto);
        return ResponseEntity.ok(aResponse(actualizado));
    }

    // DELETE /api/productos/{id} eliminar un producto del catalogo
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProducto(@PathVariable Long id) {
        productoService.eliminarProducto(id);
        return ResponseEntity.noContent().build();
    }

    // GET /api/productos/categoria/{categoria} filtrar productos por categoria
    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<List<ProductoResponse>> filtrarPorCategoria(@PathVariable String categoria) {
        List<ProductoResponse> productos = productoService.filtrarPorCategoria(categoria).stream()
                .map(this::aResponse)
                .toList();
        return ResponseEntity.ok(productos);
    }

    // GET /api/productos/categorias listar las categorias disponibles en el sitio
    @GetMapping("/categorias")
    public ResponseEntity<List<String>> obtenerCategorias() {
        List<String> categorias = productoService.obtenerCategorias();
        return ResponseEntity.ok(categorias);
    }

    private ProductoResponse aResponse(Producto producto) {
        return new ProductoResponse(
                producto.getId(),
                producto.getNombre(),
                producto.getDescripcion(),
                producto.getPrecio(),
                producto.getStock(),
                producto.getCategoria(),
                producto.getImagenUrl()
        );
    }
}
