package com.linktic.compras.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.linktic.compras.config.ProductoClient;
import com.linktic.compras.dto.request.CompraRequestDTO;
import com.linktic.compras.dto.response.CompraResponseDTO;
import com.linktic.compras.exception.GlobalExceptionHandler;
import com.linktic.compras.service.interfaces.CompraService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/compras")
public class CompraController {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private final ProductoClient productoClient;
    private final CompraService compraService;

    public CompraController(ProductoClient productoClient, CompraService compraService) {
        this.productoClient = productoClient;
        this.compraService = compraService;
    }

    @PostMapping
    public ResponseEntity<CompraResponseDTO> realizarCompra(
            @RequestHeader("Authorization") String authorization,
            @Valid @RequestBody CompraRequestDTO request) throws JsonProcessingException {

        // Quitamos "Bearer "
        String token = authorization.replace("Bearer ", "");

        CompraResponseDTO response = compraService.realizarCompra(request, token);

        request.setResourceType("compra");
        return ResponseEntity.ok(response);
    }
}
