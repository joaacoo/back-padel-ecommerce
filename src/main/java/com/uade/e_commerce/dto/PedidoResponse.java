package com.uade.e_commerce.dto;

import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PedidoResponse {
    private Long id;
    private LocalDateTime fecha;
    private Double total;
}
