package com.uade.e_commerce.dto;

public record ResenaResponse(
        Long id,
        Long productoId,
        Long usuarioId,
        String comentario,
        Integer puntuacion) {
}
