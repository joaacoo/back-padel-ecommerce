package com.uade.e_commerce.dto;

import jakarta.validation.constraints.NotNull;

public record CrearPedidoRequest(

    @NotNull(message = "El usuarioId es obligatorio")
    Long usuarioId

) {

}
