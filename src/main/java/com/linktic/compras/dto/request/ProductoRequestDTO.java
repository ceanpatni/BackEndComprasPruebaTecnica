package com.linktic.compras.dto.request;


import lombok.Data;

@Data
public class ProductoRequestDTO {
    private Data data;
    private String resourceType;

@lombok.Data
    public static class Data {
        private Attributes attributes;
    }

    @lombok.Data
    public static class Attributes {
        private Long id;
        private String nombre;
        private java.math.BigDecimal precio;
        private String descripcion;
    }
}
