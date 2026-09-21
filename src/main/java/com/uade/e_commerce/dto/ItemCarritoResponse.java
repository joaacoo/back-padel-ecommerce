package com.uade.e_commerce.dto;

import java.math.BigDecimal;

public record ItemCarritoResponse(
        Long id,
        Long productoId,
        String productoNombre,
        Integer cantidad,
        BigDecimal precioUnitario) {
}
