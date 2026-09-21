package com.uade.e_commerce.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CrearPedidoRequest {
    @NotNull(message = "El usuarioId es obligatorio")
    private Long usuarioId;
}
