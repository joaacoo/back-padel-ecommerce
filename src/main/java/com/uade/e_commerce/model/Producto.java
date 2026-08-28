package com.uade.e_commerce.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "productos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // ID autoincremental en DB
    private Long id;

    private String nombre; // Paleta addidas
    private String descripcion; // Paleta de control ideal para jugadores principiantes
    private Double precio; // 200000
    private Integer stock; // 15
    private String categoria; // Paletas
    private String imagenUrl; // URL de la foto del producto
}
