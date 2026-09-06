package com.uade.e_commerce.service;

import com.uade.e_commerce.exception.ResourceNotFoundException;
import com.uade.e_commerce.model.Producto;
import com.uade.e_commerce.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    // GET /api/productos (Obtener todos)
    public List<Producto> obtenerTodos() {
        return productoRepository.findAllByOrderByNombreAsc();
    }

    // GET /api/productos/{id} (Obtener por ID)
    public Producto obtenerPorId(Long id) {
        return productoRepository.findById(id).orElse(null); // retorna null si no existe
    }

    // POST /api/productos (Crear producto)
    @Transaccional
    public Producto crearProducto(Producto producto) {
        return productoRepository.save(producto);
    }

    // PUT /api/productos/{id} (Modificar producto)
    @Transaccional
    public Producto actualizarProducto(Long id, Producto datosActualizados) { // busca producto por ID con manejo de
                                                                              // excepcion y lo actualiza
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));

        producto.setNombre(datosActualizados.getNombre());
        producto.setDescripcion(datosActualizados.getDescripcion());
        producto.setPrecio(datosActualizados.getPrecio());
        producto.setStock(datosActualizados.getStock());
        producto.setCategoria(datosActualizados.getCategoria());
        producto.setImagenUrl(datosActualizados.getImagenUrl());

        return productoRepository.save(producto);
    }

    // DELETE /api/productos/{id} (Eliminar producto)
    @Transaccional
    public void eliminarProducto(Long id) { // busca producto por id con manejo de excepcion para eliminarlo
        if (!productoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Producto no encontrado");
        }
        productoRepository.deleteById(id);
    }

    // GET /api/productos/categoria/{categoria} (Filtrar por categoria)
    public List<Producto> filtrarPorCategoria(String categoria) {// Busca por categoria
        return productoRepository.findByCategoriaIgnoreCase(categoria);
    }

    // GET /api/productos/categorias (Listado de categorias disponibles, sin repetidas)
    public List<String> obtenerCategorias() {
        return productoRepository.findCategoriasDistinct();
    }
}
