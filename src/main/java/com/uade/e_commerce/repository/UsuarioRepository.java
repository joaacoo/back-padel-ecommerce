package com.uade.e_commerce.repository;

import com.uade.e_commerce.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByNombreUsuarioIgnoreCase(String nombreUsuario);

    Optional<Usuario> findByEmailIgnoreCase(String email);
}
