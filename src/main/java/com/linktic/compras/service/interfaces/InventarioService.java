package com.linktic.compras.service.interfaces;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.linktic.compras.dto.request.InventarioRequestDTO;
import com.linktic.compras.dto.response.InventarioResponseDTO;

public interface InventarioService {
    InventarioResponseDTO consultarCantidad(Long productoId, String token);
    InventarioResponseDTO actualizarCantidad(InventarioRequestDTO request, String token, boolean esSuma);

    InventarioResponseDTO crearInventario(InventarioRequestDTO request, String token) throws JsonProcessingException;
}
