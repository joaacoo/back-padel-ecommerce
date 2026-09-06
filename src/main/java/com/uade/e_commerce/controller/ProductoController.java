package com.uade.e_commerce.controller;

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
    public ResponseEntity<List<Producto>> obtenerTodos() {
        List<Producto> productos = productoService.obtenerTodos();
        return ResponseEntity.ok(productos);
    }

    // GET /api/productos/{id} obtener detalle de una paleta/producto especifico
    @GetMapping("/{id}")
    public ResponseEntity<Producto> obtenerPorId(@PathVariable Long id) { // Captura el {id} de la URL
        Producto producto = productoService.obtenerPorId(id);
        if (producto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(producto);
    }

    // POST /api/productos Cargar un nuevo producto de padel al catalogo
    @PostMapping
    public ResponseEntity<Producto> crearProducto(@RequestBody Producto producto) {
        Producto nuevoProducto = productoService.crearProducto(producto);
        return ResponseEntity.status(201).body(nuevoProducto);
    }

     // PUT /api/productos/{id} modificar un producto existente
    @PutMapping("/{id}")
    public ResponseEntity<Producto> modificarProducto(@PathVariable Long id, @RequestBody Producto producto) {
        Producto actualizado = productoService.actualizarProducto(id, producto);
        return ResponseEntity.ok(actualizado);
    }

     // DELETE /api/productos/{id} eliminar un producto del catalogo
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProducto(@PathVariable Long id) {
        productoService.eliminarProducto(id);
        return ResponseEntity.noContent().build();
    }

    // GET /api/productos/categoria/{categoria} filtrar productos por categoria
    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<List<Producto>> filtrarPorCategoria(@PathVariable String categoria) {
        List<Producto> productos = productoService.filtrarPorCategoria(categoria);
        return ResponseEntity.ok(productos);
    }

    // GET /api/productos/categorias listar las categorias disponibles en el sitio
    @GetMapping("/categorias")
    public ResponseEntity<List<String>> obtenerCategorias() {
        List<String> categorias = productoService.obtenerCategorias();
        return ResponseEntity.ok(categorias);
    }
}
