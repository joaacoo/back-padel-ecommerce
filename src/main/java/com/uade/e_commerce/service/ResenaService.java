package com.uade.e_commerce.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.uade.e_commerce.model.Producto;
import com.uade.e_commerce.model.Usuario;
import com.uade.e_commerce.repository.ProductoRepository;
import com.uade.e_commerce.repository.UsuarioRepository;
import com.uade.e_commerce.model.Resena;
import com.uade.e_commerce.repository.ResenaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ResenaService {

    private final ResenaRepository resenaRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;

    // POST /api/productos/{id}/resenas
    public Resena crearResena(Long productoId, Long usuarioId, String comentario, Integer puntuacion) {
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        Resena resena = new Resena();
        resena.setProducto(producto);
        resena.setUsuario(usuario);
        resena.setComentario(comentario);
        resena.setPuntuacion(puntuacion);
        return resenaRepository.save(resena);
    }

    // GET /api/productos/{id}/resenas
    public List<Resena> obtenerPorProducto(Long productoId) {
        return resenaRepository.findByProductoId(productoId);
    }

    // DELETE /api/resenas/{id}
    public void eliminarResena(Long id) {
        resenaRepository.deleteById(id);
    }
}
