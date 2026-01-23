package com.linktic.compras.dto.response;

import lombok.Data;

@Data
public class InventarioResponseDTO {
    private Data data;

    @lombok.Data
    public static class Data {
        private String type = "inventario";
        private Long id; // id del registro de inventario
        private Attributes attributes;
    }

    @lombok.Data
    public static class Attributes {
        private Long productoId;
        private Integer cantidadDisponible;
        private String error;
    }
}
