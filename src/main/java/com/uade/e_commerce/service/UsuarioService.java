package com.uade.e_commerce.service;

import org.springframework.transaction.annotation.Transactional;
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

    @Transactional
    public Usuario registrar(RegistroUsuarioRequest datos) {
        validarCampos(datos);

        String email = datos.getEmail().trim().toLowerCase();
        if (usuarioRepository.existsByEmailIgnoreCase(email)) {
            throw new ArgumentInvalidException("El email ya esta registrado");
        }

        String passwordProtegida = passwordEncoder.encode(datos.getPassword());
        Usuario usuario = Usuario.builder()
                .nombre(datos.getNombre().trim())
                .apellido(datos.getApellido().trim())
                .nombreUsuario(datos.getNombreUsuario().trim())
                .email(email)
                .password(passwordProtegida)
                .sexo(datos.getSexo())
                .fechaNacimiento(datos.getFechaNacimiento())
                .role(Role.USER)
                .build();
        return usuarioRepository.save(usuario);
    }

    public Usuario iniciarSesion(LoginUsuarioRequest datos) {
        if (datos == null || estaVacio(datos.getEmail()) || estaVacio(datos.getPassword())) {
            throw new ArgumentInvalidException("Email y password son obligatorios");
        }

        Usuario usuario = usuarioRepository.findByEmailIgnoreCase(datos.getEmail().trim())
                .orElseThrow(() -> new ArgumentInvalidException("Email o password incorrectos"));

        if (!passwordEncoder.matches(datos.getPassword(), usuario.getPassword())) {
            throw new ArgumentInvalidException("Email o password incorrectos");
        }

        return usuario;
    }

    public Usuario obtenerPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
    }

    private void validarCampos(RegistroUsuarioRequest datos) {
        if (datos == null || estaVacio(datos.getNombre()) || estaVacio(datos.getApellido())
                || estaVacio(datos.getNombreUsuario()) || estaVacio(datos.getEmail())
                || estaVacio(datos.getPassword())) {
            throw new ArgumentInvalidException("Nombre, email y password son obligatorios");
        }
    }

    private boolean estaVacio(String valor) {
        return valor == null || valor.isBlank();
    }
}
