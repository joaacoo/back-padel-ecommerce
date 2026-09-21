package com.uade.e_commerce.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.uade.e_commerce.dto.CrearResenaRequest;
import com.uade.e_commerce.dto.ResenaResponse;
import com.uade.e_commerce.model.Resena;
import com.uade.e_commerce.service.ResenaService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/resenas")
@RequiredArgsConstructor
public class ResenaController {

    private final ResenaService resenaService;

    // POST /api/resenas/producto/{id} -> Crear reseña
    @PostMapping("/producto/{id}")
    public ResponseEntity<ResenaResponse> crearResena(@PathVariable Long id, @RequestBody CrearResenaRequest request) {
        Resena nueva = resenaService.crearResena(id, request.getUsuarioId(), request.getComentario(),
                request.getPuntuacion());
        return ResponseEntity.status(HttpStatus.CREATED).body(aResponse(nueva));
    }

    // GET /api/resenas/producto/{id} -> Ver reseñas de un producto
    @GetMapping("/producto/{id}")
    public ResponseEntity<List<ResenaResponse>> verResenas(@PathVariable Long id) {
        List<ResenaResponse> resenas = resenaService.obtenerPorProducto(id).stream()
                .map(this::aResponse)
                .toList();
        return ResponseEntity.ok(resenas);
    }

    // DELETE /api/resenas/{id} -> Eliminar reseña
    @DeleteMapping("/{id}")
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
