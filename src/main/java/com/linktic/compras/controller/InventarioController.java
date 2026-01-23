package com.linktic.compras.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.linktic.compras.dto.request.InventarioRequestDTO;
import com.linktic.compras.dto.response.InventarioResponseDTO;
import com.linktic.compras.service.interfaces.InventarioService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventario")
public class InventarioController {

    private static final Logger logger = LoggerFactory.getLogger(InventarioController.class);

    private final InventarioService inventarioService;

    public InventarioController(InventarioService inventarioService) {
        this.inventarioService = inventarioService;
    }

    /**
     * Actualiza la cantidad disponible de un producto
     * Recibe JSON con productoId y cantidad
     * Token de autorización en el header
     */
    @PostMapping("/actualizar")
    public ResponseEntity<InventarioResponseDTO> actualizarCantidad(
            @RequestHeader("Authorization") String authorization,
            @Valid @RequestBody InventarioRequestDTO request) throws JsonProcessingException {

        // Quitamos "Bearer " del header
        String token = authorization.replace("Bearer ", "");

        InventarioResponseDTO response = inventarioService.actualizarCantidad(request, token, true);

        return ResponseEntity.ok(response);
    }

    /**
     * Consulta la cantidad disponible de un producto
     * Recibe JSON con productoId
     */
    @PostMapping("/consultar")
    public ResponseEntity<InventarioResponseDTO> consultarCantidad(
            @RequestHeader("Authorization") String authorization,
            @Valid @RequestBody InventarioRequestDTO request) throws JsonProcessingException {

        String token = authorization.replace("Bearer ", "");

        InventarioResponseDTO response = inventarioService.consultarCantidad(
                request.getData().getAttributes().getProductoId(),
                token
        );

        return ResponseEntity.ok(response);
    }
    @PostMapping("/crear")
    public ResponseEntity<InventarioResponseDTO> crearInventario(
            @RequestHeader("Authorization") String authorization,
            @Valid @RequestBody InventarioRequestDTO request) throws JsonProcessingException {

        String token = authorization.replace("Bearer ", "");
        InventarioResponseDTO response = inventarioService.crearInventario(request, token);
        return ResponseEntity.ok(response);
    }

}

