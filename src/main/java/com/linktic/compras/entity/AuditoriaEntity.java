package com.linktic.compras.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "auditoria")
@Data
public class AuditoriaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String entidad;
    private Long entidadId;

    @Column(columnDefinition = "TEXT")
    private String requestJson;

    @Column(columnDefinition = "TEXT")
    private String responseJson;

    private String mensaje;
    private Boolean exitoso;
    @Column(name = "fecha_evento", nullable = false)
    private LocalDateTime fecha;
}
