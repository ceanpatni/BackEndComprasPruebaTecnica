package com.linktic.compras.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "inventario")
@Data
public class InventarioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ID del producto que vive en BackendProductos
    @Column(name = "producto_id", nullable = false)
    private Long productoId;

    @Column(nullable = false)
    private Integer cantidad;
}
