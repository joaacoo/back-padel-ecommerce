package com.uade.e_commerce.dto;

import java.time.LocalDate;

public record UsuarioResponse(Long id, String nombre, String email, String sexo, LocalDate fechaNacimiento) {
}
