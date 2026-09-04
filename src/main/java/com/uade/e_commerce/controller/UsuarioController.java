package com.uade.e_commerce.controller;

import com.uade.e_commerce.dto.LoginUsuarioRequest;
import com.uade.e_commerce.dto.RegistroUsuarioRequest;
import com.uade.e_commerce.dto.UsuarioResponse;
import com.uade.e_commerce.model.Usuario;
import com.uade.e_commerce.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/register")
    public ResponseEntity<UsuarioResponse> registrar(@RequestBody RegistroUsuarioRequest datos) {
        Usuario usuario = usuarioService.registrar(datos);
        return ResponseEntity.status(HttpStatus.CREATED).body(aResponse(usuario));
    }

    @PostMapping("/login")
    public ResponseEntity<UsuarioResponse> iniciarSesion(@RequestBody LoginUsuarioRequest datos) {
        return ResponseEntity.ok(aResponse(usuarioService.iniciarSesion(datos)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(aResponse(usuarioService.obtenerPorId(id)));
    }

    private UsuarioResponse aResponse(Usuario usuario) {
        return new UsuarioResponse(usuario.getId(), usuario.getNombre(), usuario.getEmail(), usuario.getSexo(),
                usuario.getFechaNacimiento());
    }

    @ExceptionHandler(UsuarioService.EmailDuplicadoException.class)
    public ResponseEntity<Map<String, String>> emailDuplicado() {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("error", "El email ya esta registrado"));
    }

    @ExceptionHandler(UsuarioService.DatosInvalidosException.class)
    public ResponseEntity<Map<String, String>> datosInvalidos() {
        return ResponseEntity.badRequest()
                .body(Map.of("error", "Nombre, email y password son obligatorios"));
    }

    @ExceptionHandler(UsuarioService.CredencialesInvalidasException.class)
    public ResponseEntity<Map<String, String>> credencialesInvalidas() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Email o password incorrectos"));
    }

    @ExceptionHandler(UsuarioService.UsuarioNoEncontradoException.class)
    public ResponseEntity<Map<String, String>> usuarioNoEncontrado() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "Usuario no encontrado"));
    }
}
