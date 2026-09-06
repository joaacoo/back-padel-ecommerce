package com.uade.e_commerce.service;

import com.uade.e_commerce.exception.ArgumentInvalidException;
import com.uade.e_commerce.exception.ResourceNotFoundException;
import com.uade.e_commerce.model.ItemCarrito;
import com.uade.e_commerce.model.Pedido;
import com.uade.e_commerce.model.Producto;
import com.uade.e_commerce.model.Usuario;
import com.uade.e_commerce.repository.PedidoRepository;
import com.uade.e_commerce.repository.ProductoRepository;
import com.uade.e_commerce.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final UsuarioRepository usuarioRepository;
    private final CarritoService carritoService;
    private final ProductoRepository productoRepository;

    public PedidoService(PedidoRepository pedidoRepository,
                         UsuarioRepository usuarioRepository,
                         CarritoService carritoService,
                         ProductoRepository productoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.usuarioRepository = usuarioRepository;
        this.carritoService = carritoService;
        this.productoRepository = productoRepository;
    }

    // GET /api/pedidos/{id}
    public Pedido obtenerPorId(Long id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado"));
    }

    // GET /api/users/{id}/pedidos
    public List<Pedido> obtenerPedidosDeUsuario(Long usuarioId) {
        if (!usuarioRepository.existsById(usuarioId)) {
            throw new ResourceNotFoundException("Usuario no encontrado");
        }
        return pedidoRepository.findByUsuarioId(usuarioId);
    }

    // POST /api/pedidos
    @Transaccional
    public Pedido crearPedido(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        List<ItemCarrito> items = carritoService.getAllItemCarritos(usuarioId);
        if (items.isEmpty()) {
            throw new ArgumentInvalidException("El carrito está vacío");
        }

        double total = 0.0;
        for (ItemCarrito item : items) {
            Producto producto = item.getProducto();
            if (producto.getStock() < item.getCantidad()) {
                throw new ArgumentInvalidException("Stock insuficiente para el producto: " + producto.getNombre());
            }
            producto.setStock(producto.getStock() - item.getCantidad());
            productoRepository.save(producto);
            total += item.getPrecioUnitario().doubleValue() * item.getCantidad();
        }

        Pedido pedido = new Pedido();
        pedido.setUsuario(usuario);
        pedido.setFecha(LocalDateTime.now());
        pedido.setTotal(total);

        Pedido guardado = pedidoRepository.save(pedido);
        
        carritoService.vaciarCarrito(usuarioId);

        return guardado;
    }
}
