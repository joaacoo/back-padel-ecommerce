package com.uade.e_commerce.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.uade.e_commerce.model.ItemCarrito;

public interface ItemCarritoRepository extends JpaRepository<ItemCarrito, Long> {
    List<ItemCarrito> findByCarrito_Id(Long carritoId);

    Optional<ItemCarrito> findByCarrito_IdAndProducto_Id(Long carritoId, Long productoId);

    Optional<ItemCarrito> findByCarritoIdAndProductoId(Long id, Long productoId);
}