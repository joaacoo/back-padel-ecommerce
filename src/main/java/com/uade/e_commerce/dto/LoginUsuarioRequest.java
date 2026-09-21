package com.uade.e_commerce.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginUsuarioRequest {
    private String email;
    private String password;
}
