package com.linktic.compras.dto.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductoResponseDTO {
    private Data data;

 @lombok.Data
    public static class Data {
        private String type = "producto";
        private Long id;
        private Attributes attributes;
    }
    @lombok.Data
    public static class Attributes {
        private Long id;
        private String nombre;
        private BigDecimal precio;
        private String descripcion;
        // campo para errores
        private String error;
    }
}
