package com.uade.e_commerce.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.uade.e_commerce.model.Carrito;
import com.uade.e_commerce.model.ItemCarrito;
import com.uade.e_commerce.model.Usuario;
import com.uade.e_commerce.repository.CarritoRepository;
import com.uade.e_commerce.repository.ItemCarritoRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor

public class CarritoService {
    private final CarritoRepository carritoRepository;
    private final ItemCarritoRepository itemCarritoRepository;
    private final UsuarioService usuarioService;
    private final ProductoService productoService;

    public Carrito getCarritoByUsuarioId(Long usuarioId) {
        return carritoRepository.findByUsuario_Id(usuarioId)
                .orElseGet(() -> createCarrito(usuarioId));
    }

    private Carrito createCarrito(Long usuarioId) {
        Carrito carrito = new Carrito();
        Usuario usuario = usuarioService.obtenerPorId(usuarioId);
        carrito.setUsuario(usuario);
        return carritoRepository.save(carrito);
    }

    public List<ItemCarrito> getAllItemCarritos(Long usuarioId) {
        Carrito carrito = getCarritoByUsuarioId(usuarioId);
        return itemCarritoRepository.findByCarrito_Id(carrito.getId());
    }

    public ItemCarrito addItemCarrito(Long usuarioId, Long productoId, Integer cantidad, BigDecimal precioUnitario) {

        Carrito carrito = getCarritoByUsuarioId(usuarioId);
        com.uade.e_commerce.model.Producto producto = productoService.obtenerPorId(productoId);
        if (producto == null) {
            throw new RuntimeException("Producto no encontrado");
        }

        Optional<ItemCarrito> existente = itemCarritoRepository.findByCarrito_IdAndProducto_Id(carrito.getId(),
                productoId);

        if (existente.isPresent()) {
            ItemCarrito item = existente.get();
            int nuevaCantidad = item.getCantidad() + cantidad;
            if (nuevaCantidad > producto.getStock()) {
                throw new RuntimeException("Stock insuficiente para el producto: " + producto.getNombre());
            }
            item.setCantidad(nuevaCantidad); // ← SUMA
            return itemCarritoRepository.save(item);
        }

        if (cantidad > producto.getStock()) {
            throw new RuntimeException("Stock insuficiente para el producto: " + producto.getNombre());
        }

        ItemCarrito nuevo = new ItemCarrito();
        nuevo.setCarrito(carrito);
        nuevo.setProducto(producto);
        nuevo.setCantidad(cantidad);
        nuevo.setPrecioUnitario(precioUnitario);
        return itemCarritoRepository.save(nuevo);
    }

    public ItemCarrito actualizarCantidad(Long itemId, Integer cantidad) {
        Optional<ItemCarrito> existente = itemCarritoRepository.findById(itemId);
        if (existente.isPresent()) {
            ItemCarrito item = existente.get();
            if (cantidad > item.getProducto().getStock()) {
                throw new RuntimeException("Stock insuficiente para el producto: " + item.getProducto().getNombre());
            }
            item.setCantidad(cantidad);
            return itemCarritoRepository.save(item);
        }
        throw new RuntimeException("Item no encontrado");
    }

    public void eliminarItem(Long usuarioId, Long productoId) {
        Carrito carrito = getCarritoByUsuarioId(usuarioId);
        Optional<ItemCarrito> existente = itemCarritoRepository.findByCarrito_IdAndProducto_Id(carrito.getId(),
                productoId);

        existente.ifPresent(itemCarritoRepository::delete);
    }

    @Transactional
    public void vaciarCarrito(Long usuarioId) {
        Carrito carrito = getCarritoByUsuarioId(usuarioId);
        List<ItemCarrito> items = itemCarritoRepository.findByCarrito_Id(carrito.getId());
        itemCarritoRepository.deleteAll(items);
    }
}