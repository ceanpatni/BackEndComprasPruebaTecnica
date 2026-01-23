package com.linktic.compras.service.interfaces;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.linktic.compras.dto.request.CompraRequestDTO;
import com.linktic.compras.dto.response.CompraResponseDTO;

public interface CompraService {
    CompraResponseDTO realizarCompra(CompraRequestDTO request, String token) throws JsonProcessingException;
}