package com.uade.e_commerce.repository;

import com.uade.e_commerce.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import org.springframework.data.jpa.repository.Query;

@Repository
// al heredar de JpaRepository, ya tienes listos metodos como save(), findAll()
// y findById()
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    List<Producto> findByCategoriaIgnoreCase(String categoria);// Spring Data arma la query automaticamente a partir del nombre del metodo

    // Devuelve todos los productos ordenados alfabeticamente por nombre
    List<Producto> findAllByOrderByNombreAsc();

    // Devuelve las categorias distintas que existen actualmente en la tabla, ordenadas alfabeticamente
    @Query("SELECT DISTINCT p.categoria FROM Producto p ORDER BY p.categoria ASC")
    List<String> findCategoriasDistinct();
}
