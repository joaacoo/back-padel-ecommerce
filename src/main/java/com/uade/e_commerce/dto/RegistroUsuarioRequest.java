package com.uade.e_commerce.dto;

import java.time.LocalDate;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistroUsuarioRequest {
    private String nombre;
    private String apellido;
    private String nombreUsuario;
    private String email;
    private String password;
    private String sexo;
    private LocalDate fechaNacimiento;
}
