package com.uade.e_commerce.repository;

import com.uade.e_commerce.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
// al heredar de JpaRepository, ya tienes listos metodos como save(), findAll()
// y findById()
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    List<Producto> findByCategoriaIgnoreCase(String categoria);// Spring Data arma la query automaticamente a partir del nombre del metodo

}
