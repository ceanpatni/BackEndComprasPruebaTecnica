package com.linktic.compras.dto.request;

import lombok.Data;

@Data
public class ErrorResponseDTO {
    private Data data;

    @lombok.Data
    public static class Data {
        private String type; // "compra", "producto", etc.
        private Long id;     // null si es un error
        private Attributes attributes;

        public Data(String type) {
            this.type = type;
            this.attributes = new Attributes();
        }
    }

    @lombok.Data
    public static class Attributes {
        private String error;
    }

    // Constructor que recibe tipo dinámico y mensaje
    public ErrorResponseDTO(String type, String errorMessage) {
        this.data = new Data(type);
        this.data.getAttributes().setError(errorMessage);
    }
}
