package com.uade.e_commerce.dto;

public record ProductoResponse(
        Long id,
        String nombre,
        String descripcion,
        Double precio,
        Integer stock,
        String categoria,
        String imagenUrl) {
}
