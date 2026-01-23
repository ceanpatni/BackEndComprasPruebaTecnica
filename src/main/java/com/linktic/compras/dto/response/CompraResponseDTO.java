package com.linktic.compras.dto.response;

import lombok.Data;

import java.math.BigDecimal;
@Data
public class CompraResponseDTO {
    private Data data;
@lombok.Data
    public static class Data {
        private String type = "compra";
        private Long id;
        private Attributes attributes;
    }
    @lombok.Data
    public static class Attributes {
        private Long productoId;
        private Integer cantidad;
        private BigDecimal total;
        // campo para errores
        private String error;
    }
}
