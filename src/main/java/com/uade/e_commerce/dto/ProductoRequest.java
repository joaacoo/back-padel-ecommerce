package com.uade.e_commerce.dto;

public record ProductoRequest(
        String nombre,
        String descripcion,
        Double precio,
        Integer stock,
        String categoria,
        String imagenUrl) {
}
