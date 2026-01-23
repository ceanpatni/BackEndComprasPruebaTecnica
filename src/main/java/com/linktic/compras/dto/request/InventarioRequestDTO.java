package com.linktic.compras.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class InventarioRequestDTO {
    private Data data;
    private String resourceType;

    @lombok.Data
    public static class Data {
        private Attributes attributes;
    }

    @lombok.Data
    public static class Attributes {
        @NotNull(message = "El campo 'productoId' es obligatorio")
        private Long productoId;

        @NotNull(message = "El campo 'cantidad' es obligatorio")
        @Min(value = 1, message = "La cantidad debe ser mayor a cero")
        private Integer cantidad;

    }
}
