package com.linktic.compras.config;

import com.linktic.compras.dto.request.ProductoRequestDTO;
import com.linktic.compras.dto.response.ProductoResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ProductoClient {

    @Value("${microservices.productos.url}")
    private String url;

    private final RestTemplate restTemplate;

    public ProductoClient(RestTemplate restTemplate){
        this.restTemplate = restTemplate;
    }


    public ProductoResponseDTO obtenerProducto(Long id, String token){

        // Construir el request DTO
        ProductoRequestDTO request = new ProductoRequestDTO();
        ProductoRequestDTO.Data data = new ProductoRequestDTO.Data();
        ProductoRequestDTO.Attributes attr = new ProductoRequestDTO.Attributes();

        attr.setId(id);
        data.setAttributes(attr);
        request.setData(data);
        request.setResourceType("producto");

        // 🔹 Headers con Bearer token
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.add("Content-Type", "application/json");

        // 🔹 Enviar body + headers
        HttpEntity<ProductoRequestDTO> entity =
                new HttpEntity<>(request, headers);

        // 🔹 Llamada POST
        ResponseEntity<ProductoResponseDTO> response =
                restTemplate.exchange(
                        url + "/api/productos/obtener",
                        HttpMethod.POST,
                        entity,
                        ProductoResponseDTO.class
                );

        return response.getBody();
    }
}
