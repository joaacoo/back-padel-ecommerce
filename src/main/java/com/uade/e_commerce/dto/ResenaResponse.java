package com.uade.e_commerce.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResenaResponse {
    private Long id;
    private Long productoId;
    private Long usuarioId;
    private String comentario;
    private Integer puntuacion;
}
