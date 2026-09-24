package com.uade.e_commerce.controller;

import jakarta.validation.Valid;
import com.uade.e_commerce.dto.ProductoRequest;
import com.uade.e_commerce.dto.ProductoResponse;
import com.uade.e_commerce.model.Producto;
import com.uade.e_commerce.service.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @Operation(summary = "Listar productos", description = "Obtiene todos los productos del catálogo ordenados por nombre.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado de productos, que puede estar vacío")
    })
    public ResponseEntity<List<ProductoResponse>> obtenerTodos() {
        List<ProductoResponse> productos = productoService.obtenerTodos().stream()
                .map(this::aResponse)
                .toList();
        return ResponseEntity.ok(productos);
    }

    // GET /api/productos/{id} obtener detalle de una paleta/producto especifico
    @GetMapping("/{id}")
    @Operation(summary = "Obtener producto", description = "Obtiene el detalle de un producto por su identificador.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Producto encontrado"),
            @ApiResponse(responseCode = "400", description = "El identificador no es un número entero válido",
                    content = @Content(mediaType = "application/json", schema = @Schema(type = "object"))),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado", content = @Content)
    })
    public ResponseEntity<ProductoResponse> obtenerPorId(@PathVariable Long id) { // Captura el {id} de la URL
        Producto producto = productoService.obtenerPorId(id);
        if (producto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(aResponse(producto));
    }

    // POST /api/productos Cargar un nuevo producto de padel al catalogo
    @PostMapping
    @Operation(summary = "Crear producto", description = "Agrega un nuevo producto al catálogo con los datos recibidos.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Producto creado"),
            @ApiResponse(responseCode = "400", description = "El cuerpo de la solicitud es inválido o incumple las validaciones del producto",
                    content = @Content(mediaType = "application/json", schema = @Schema(type = "object")))
    })
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
    @Operation(summary = "Modificar producto", description = "Actualiza los datos de un producto existente identificado por su ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Producto actualizado"),
            @ApiResponse(responseCode = "400", description = "El identificador o el cuerpo de la solicitud son inválidos, o se incumplen las validaciones del producto",
                    content = @Content(mediaType = "application/json", schema = @Schema(type = "object"))),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(type = "object")))
    })
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
    @Operation(summary = "Eliminar producto", description = "Elimina del catálogo el producto identificado por su ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Producto eliminado", content = @Content),
            @ApiResponse(responseCode = "400", description = "El identificador no es un número entero válido",
                    content = @Content(mediaType = "application/json", schema = @Schema(type = "object"))),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(type = "object")))
    })
    public ResponseEntity<Void> eliminarProducto(@PathVariable Long id) {
        productoService.eliminarProducto(id);
        return ResponseEntity.noContent().build();
    }

    // GET /api/productos/categoria/{categoria} filtrar productos por categoria
    @GetMapping("/categoria/{categoria}")
    @Operation(summary = "Filtrar productos por categoría", description = "Obtiene los productos de una categoría sin distinguir mayúsculas y minúsculas.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado de productos de la categoría, que puede estar vacío")
    })
    public ResponseEntity<List<ProductoResponse>> filtrarPorCategoria(@PathVariable String categoria) {
        List<ProductoResponse> productos = productoService.filtrarPorCategoria(categoria).stream()
                .map(this::aResponse)
                .toList();
        return ResponseEntity.ok(productos);
    }

    // GET /api/productos/categorias listar las categorias disponibles en el sitio
    @GetMapping("/categorias")
    @Operation(summary = "Listar categorías", description = "Obtiene las categorías existentes en el catálogo sin valores repetidos.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado de categorías, que puede estar vacío")
    })
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
