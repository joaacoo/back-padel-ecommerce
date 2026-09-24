package com.uade.e_commerce.controller;

import jakarta.validation.Valid;
import com.uade.e_commerce.dto.LoginUsuarioRequest;
import com.uade.e_commerce.dto.RegistroUsuarioRequest;
import com.uade.e_commerce.dto.UsuarioResponse;
import com.uade.e_commerce.model.Usuario;
import com.uade.e_commerce.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/register")
    @Operation(summary = "Registrar un usuario",
            description = "Crea un usuario con los datos de registro y devuelve su información pública.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuario registrado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de registro inválidos, campos obligatorios ausentes, "
                    + "email ya registrado o cuerpo de la solicitud inválido",
                    content = @Content(mediaType = "application/json", schema = @Schema(type = "object")))
    })
    public ResponseEntity<UsuarioResponse> registrar(@Valid @RequestBody RegistroUsuarioRequest datos) {
        Usuario usuario = usuarioService.registrar(datos);
        return ResponseEntity.status(HttpStatus.CREATED).body(aResponse(usuario));
    }

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión",
            description = "Verifica el email y la contraseña y devuelve la información pública del usuario.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Credenciales verificadas correctamente"),
            @ApiResponse(responseCode = "400", description = "Email o contraseña incorrectos, datos de acceso inválidos "
                    + "o cuerpo de la solicitud inválido",
                    content = @Content(mediaType = "application/json", schema = @Schema(type = "object")))
    })
    public ResponseEntity<UsuarioResponse> iniciarSesion(@Valid @RequestBody LoginUsuarioRequest datos) {
        return ResponseEntity.ok(aResponse(usuarioService.iniciarSesion(datos)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un usuario",
            description = "Devuelve la información pública del usuario identificado por su ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
            @ApiResponse(responseCode = "400", description = "El ID del usuario no es un número entero válido",
                    content = @Content(mediaType = "application/json", schema = @Schema(type = "object"))),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(type = "object")))
    })
    public ResponseEntity<UsuarioResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(aResponse(usuarioService.obtenerPorId(id)));
    }

    private UsuarioResponse aResponse(Usuario usuario) {
        return new UsuarioResponse(usuario.getId(), usuario.getNombre(), usuario.getEmail(), usuario.getSexo(),
                usuario.getFechaNacimiento());
    }
}
