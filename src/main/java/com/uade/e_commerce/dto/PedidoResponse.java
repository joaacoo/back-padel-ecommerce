package com.uade.e_commerce.dto;

import java.time.LocalDateTime;

public record PedidoResponse(Long id, LocalDateTime fecha, Double total) {
}
