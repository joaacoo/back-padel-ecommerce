package com.uade.e_commerce.service;

import com.uade.e_commerce.dto.LoginUsuarioRequest;
import com.uade.e_commerce.dto.RegistroUsuarioRequest;
import com.uade.e_commerce.exception.ArgumentInvalidException;
import com.uade.e_commerce.exception.ResourceNotFoundException;
import com.uade.e_commerce.model.Usuario;
import com.uade.e_commerce.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import com.uade.e_commerce.model.Role;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transaccional
    public Usuario registrar(RegistroUsuarioRequest datos) {
        validarCampos(datos);

        String email = datos.email().trim().toLowerCase();
        if (usuarioRepository.existsByEmailIgnoreCase(email)) {
            throw new ArgumentInvalidException("El email ya esta registrado");
        }

        String passwordProtegida = passwordEncoder.encode(datos.password());
        Usuario usuario = Usuario.builder()
        .nombre(datos.nombre().trim())
        .apellido(datos.apellido().trim())
        .nombreUsuario(datos.nombreUsuario().trim())
        .email(email)
        .password(passwordProtegida)
        .sexo(datos.sexo())
        .fechaNacimiento(datos.fechaNacimiento())
        .role(Role.USER)
        .build();
return usuarioRepository.save(usuario);
    }

    public Usuario iniciarSesion(LoginUsuarioRequest datos) {
        if (datos == null || estaVacio(datos.email()) || estaVacio(datos.password())) {
            throw new ArgumentInvalidException("Email y password son obligatorios");
        }

        Usuario usuario = usuarioRepository.findByEmailIgnoreCase(datos.email().trim())
                .orElseThrow(() -> new ArgumentInvalidException("Email o password incorrectos"));

        if (!passwordEncoder.matches(datos.password(), usuario.getPassword())) {
            throw new ArgumentInvalidException("Email o password incorrectos");
        }

        return usuario;
    }

    public Usuario obtenerPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
    }

    private void validarCampos(RegistroUsuarioRequest datos) {
        if (datos == null || estaVacio(datos.nombre()) || estaVacio(datos.apellido()) || estaVacio(datos.nombreUsuario()) || estaVacio(datos.email())
                || estaVacio(datos.password())) {
            throw new ArgumentInvalidException("Nombre, email y password son obligatorios");
        }
    }

    private boolean estaVacio(String valor) {
        return valor == null || valor.isBlank();
    }
}
