package com.uade.e_commerce;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import com.uade.e_commerce.model.*;
import com.uade.e_commerce.repository.*;
import com.uade.e_commerce.service.*;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class PedidoServiceTests {
    @Test
    void totalUsesExactDecimalArithmetic() {
        PedidoRepository pedidos = mock(PedidoRepository.class);
        UsuarioRepository usuarios = mock(UsuarioRepository.class);
        ProductoRepository productos = mock(ProductoRepository.class);
        CarritoService carrito = mock(CarritoService.class);
        Usuario user = new Usuario("User", "Test", "user", "user@test.com", "password", null, null);
        when(usuarios.findById(1L)).thenReturn(Optional.of(user));
        Producto producto = new Producto();
        producto.setStock(10);
        ItemCarrito first = new ItemCarrito();
        first.setProducto(producto);
        first.setCantidad(3);
        first.setPrecioUnitario(new BigDecimal("0.10"));
        ItemCarrito second = new ItemCarrito();
        second.setProducto(producto);
        second.setCantidad(1);
        second.setPrecioUnitario(new BigDecimal("0.20"));
        when(carrito.getAllItemCarritos(1L)).thenReturn(List.of(first, second));
        when(pedidos.save(any(Pedido.class))).thenAnswer(call -> call.getArgument(0));
        Pedido result = new PedidoService(pedidos, usuarios, carrito, productos).crearPedido(1L);
        assertThat(result.getTotal()).isEqualByComparingTo("0.50");
        assertThat(producto.getStock()).isEqualTo(6);
        verify(carrito).vaciarCarrito(1L);
    }
}
