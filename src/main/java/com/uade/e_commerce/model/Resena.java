package com.uade.e_commerce.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "resenas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Resena {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id")
    private Producto producto; // a que producto pertenece la reseña

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario; // quien la escribió

    private String comentario; // "Excelente paleta, buen control"
    private Integer puntuacion; // 1 a 5
}
