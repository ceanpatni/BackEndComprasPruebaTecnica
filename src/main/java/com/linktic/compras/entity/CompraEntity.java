package com.linktic.compras.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "compras")
@Data
public class CompraEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ID del producto que vive en BackendProductos
    @Column(name = "producto_id", nullable = false)
    private Long productoId;

    private Integer cantidad;

    @Column(name = "precio_unitario")
    private BigDecimal precioUnitario;

    private BigDecimal total;

    @Column(name = "fecha_compra")
    private LocalDateTime fechaCompra;
}
