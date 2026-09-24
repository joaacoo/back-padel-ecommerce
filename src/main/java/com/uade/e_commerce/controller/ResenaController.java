package com.uade.e_commerce.controller;

import jakarta.validation.Valid;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.uade.e_commerce.dto.CrearResenaRequest;
import com.uade.e_commerce.dto.ResenaResponse;
import com.uade.e_commerce.model.Resena;
import com.uade.e_commerce.service.ResenaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/resenas")
@RequiredArgsConstructor
public class ResenaController {

    private final ResenaService resenaService;

    // POST /api/resenas/producto/{id} -> Crear reseña
    @PostMapping("/producto/{id}")
    @Operation(summary = "Crear reseña", description = "Crea una reseña de un producto asociada al usuario indicado, con un comentario y una puntuación de 1 a 5.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Reseña creada"),
            @ApiResponse(responseCode = "400", description = "El identificador o el cuerpo de la solicitud son inválidos, o se incumplen las validaciones de la reseña",
                    content = @Content(mediaType = "application/json", schema = @Schema(type = "object"))),
            @ApiResponse(responseCode = "404", description = "Producto o usuario no encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(type = "object")))
    })
    public ResponseEntity<ResenaResponse> crearResena(@PathVariable Long id, @Valid @RequestBody CrearResenaRequest request) {
        Resena nueva = resenaService.crearResena(id, request.getUsuarioId(), request.getComentario(),
                request.getPuntuacion());
        return ResponseEntity.status(HttpStatus.CREATED).body(aResponse(nueva));
    }

    // GET /api/resenas/producto/{id} -> Ver reseñas de un producto
    @GetMapping("/producto/{id}")
    @Operation(summary = "Listar reseñas de un producto", description = "Obtiene las reseñas asociadas al identificador de producto indicado. Devuelve una lista vacía si no hay reseñas, incluso si el producto no existe.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado de reseñas, que puede estar vacío"),
            @ApiResponse(responseCode = "400", description = "El identificador no es un número entero válido",
                    content = @Content(mediaType = "application/json", schema = @Schema(type = "object")))
    })
    public ResponseEntity<List<ResenaResponse>> verResenas(@PathVariable Long id) {
        List<ResenaResponse> resenas = resenaService.obtenerPorProducto(id).stream()
                .map(this::aResponse)
                .toList();
        return ResponseEntity.ok(resenas);
    }

    // DELETE /api/resenas/{id} -> Eliminar reseña
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar reseña", description = "Elimina la reseña indicada por su identificador. Si no existe, la solicitud también finaliza sin contenido.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Solicitud de eliminación completada", content = @Content),
            @ApiResponse(responseCode = "400", description = "El identificador no es un número entero válido",
                    content = @Content(mediaType = "application/json", schema = @Schema(type = "object")))
    })
    public ResponseEntity<Void> eliminarResena(@PathVariable Long id) {
        resenaService.eliminarResena(id);
        return ResponseEntity.noContent().build();
    }

    private ResenaResponse aResponse(Resena resena) {
        Long productoId = null;
        if (resena.getProducto() != null) {
            productoId = resena.getProducto().getId();
        }
        Long usuarioId = null;
        if (resena.getUsuario() != null) {
            usuarioId = resena.getUsuario().getId();
        }
        return new ResenaResponse(
                resena.getId(),
                productoId,
                usuarioId,
                resena.getComentario(),
                resena.getPuntuacion()
        );
    }
}
