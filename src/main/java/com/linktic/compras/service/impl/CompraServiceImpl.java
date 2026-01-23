package com.linktic.compras.service.impl;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.linktic.compras.config.ProductoClient;
import com.linktic.compras.dto.request.CompraRequestDTO;
import com.linktic.compras.dto.request.InventarioRequestDTO;
import com.linktic.compras.dto.response.CompraResponseDTO;
import com.linktic.compras.dto.response.InventarioResponseDTO;
import com.linktic.compras.dto.response.ProductoResponseDTO;
import com.linktic.compras.entity.AuditoriaEntity;
import com.linktic.compras.entity.CompraEntity;
import com.linktic.compras.entity.InventarioEntity;
import com.linktic.compras.exception.BusinessException;
import com.linktic.compras.repository.AuditoriaRepository;
import com.linktic.compras.repository.CompraRepository;
import com.linktic.compras.repository.InventarioRepository;
import com.linktic.compras.service.interfaces.CompraService;
import com.linktic.compras.service.interfaces.InventarioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class CompraServiceImpl implements CompraService {
    private static final Logger logger = LoggerFactory.getLogger(CompraServiceImpl.class);

    private final CompraRepository compraRepository;
    private final AuditoriaRepository auditoriaRepository;
    private final InventarioRepository inventarioRepository;
    private final ObjectMapper objectMapper;
    private final ProductoClient productoClient;
    private final InventarioService inventarioService;

    public CompraServiceImpl(CompraRepository compraRepository,
                             AuditoriaRepository auditoriaRepository,
                             InventarioRepository inventarioRepository, ObjectMapper objectMapper, ProductoClient productoClient, InventarioService inventarioService) {
        this.compraRepository = compraRepository;
        this.auditoriaRepository = auditoriaRepository;
        this.inventarioRepository = inventarioRepository;
        this.objectMapper = objectMapper;
        this.productoClient = productoClient;
        this.inventarioService = inventarioService;
    }

    @Override
    @Transactional
    public CompraResponseDTO realizarCompra(CompraRequestDTO request,String token) throws JsonProcessingException {
        AuditoriaEntity auditoria = new AuditoriaEntity();
        auditoria.setFecha(LocalDateTime.now());
        auditoria.setEntidad("compra");
        CompraResponseDTO responseDTO = new CompraResponseDTO();
        CompraResponseDTO.Data data = new CompraResponseDTO.Data();
        data.setType("compra");
        CompraResponseDTO.Attributes attributes = new CompraResponseDTO.Attributes();

        try {
            validarRequest(request);

            ProductoResponseDTO productoResponse =
                    productoClient.obtenerProducto(
                            request.getData().getAttributes().getProductoId(),
                            token
                    );
            logger.info("ProductoResponse recibido: {}", productoResponse);
            if (productoResponse.getData() == null
                    || productoResponse.getData().getId() == null
                    || (productoResponse.getData().getAttributes() != null
                    && "El producto no existe".equals(productoResponse.getData().getAttributes().getError()))) {
                throw new BusinessException("El producto no existe");
            }


            // Verificar inventario
            InventarioEntity inventario = inventarioRepository
                    .findByProductoId(productoResponse.getData().getId())
                    .orElseThrow(() -> new BusinessException("El producto no existe en inventario"));

            // Validar cantidad en inventario
            if (inventario.getCantidad() <= 0) {
                throw new BusinessException("Stock insuficiente");
            }

            int cantidadSolicitada = request.getData().getAttributes().getCantidad();
            if (inventario.getCantidad() < cantidadSolicitada) {
                throw new BusinessException("Stock insuficiente");
            }
            CompraEntity compra = new CompraEntity();
            compra.setProductoId(productoResponse.getData().getId());
            compra.setCantidad(request.getData().getAttributes().getCantidad());
            compra.setFechaCompra(LocalDateTime.now());
            compra.setPrecioUnitario(productoResponse.getData().getAttributes().getPrecio());
            BigDecimal cantidadBD = BigDecimal.valueOf(request.getData().getAttributes().getCantidad());
            BigDecimal total = cantidadBD.multiply(productoResponse.getData().getAttributes().getPrecio());
            compra.setTotal(total);

            CompraEntity compraGuardada = compraRepository.save(compra);

            attributes.setProductoId(compraGuardada.getProductoId());
            attributes.setCantidad(compraGuardada.getCantidad());
            attributes.setTotal(compraGuardada.getTotal());
             if(compraGuardada.getId()==null){
                 throw new BusinessException("Compra rechazada");
             }

            InventarioRequestDTO inventarioRequest = new InventarioRequestDTO();
            InventarioRequestDTO.Data dataInv = new InventarioRequestDTO.Data();
            InventarioRequestDTO.Attributes attributesInv = new InventarioRequestDTO.Attributes();

            attributesInv.setProductoId(compraGuardada.getProductoId());
            attributesInv.setCantidad(compraGuardada.getCantidad());
            dataInv.setAttributes(attributesInv);
            inventarioRequest.setData(dataInv);

            InventarioResponseDTO invResponse = inventarioService.actualizarCantidad(inventarioRequest, token, false);

            if (invResponse.getData().getAttributes().getError() != null) {
                throw new BusinessException("No se pudo actualizar inventario: "
                        + invResponse.getData().getAttributes().getError());
            }
            data.setId(compraGuardada.getId());
            data.setAttributes(attributes);
            responseDTO.setData(data);
            auditoria.setExitoso(true);
            auditoria.setEntidad("compra");
            auditoria.setRequestJson(objectMapper.writeValueAsString(request));
            auditoria.setResponseJson(objectMapper.writeValueAsString(responseDTO));
            auditoria.setMensaje("Compra procesada exitosamente");

            return responseDTO;

        } catch (BusinessException ex) {
            // enviar error dentro del mismo DTO
            attributes.setError(ex.getMessage());
            data.setAttributes(attributes);
            responseDTO.setData(data);
            auditoria.setEntidad("Compra");
            auditoria.setExitoso(false);
            auditoria.setRequestJson(objectMapper.writeValueAsString(request));
            auditoria.setResponseJson(objectMapper.writeValueAsString(responseDTO));
            auditoria.setMensaje("Error al procesar compra: " + ex.getMessage());

            return responseDTO;

        } catch (Exception ex) {
            attributes.setError("Error interno del servidor");
            data.setAttributes(attributes);
            responseDTO.setData(data);
            auditoria.setEntidad("Compra");
            auditoria.setExitoso(false);
            auditoria.setRequestJson(objectMapper.writeValueAsString(request));
            auditoria.setResponseJson(objectMapper.writeValueAsString(responseDTO));
            auditoria.setMensaje("Error inesperado: " + ex.getMessage());

            return responseDTO;

        } finally {
            auditoriaRepository.save(auditoria);
        }
    }



    private void validarRequest(CompraRequestDTO request) {

        if (request == null || request.getData() == null || request.getData().getAttributes() == null) {
            throw new BusinessException("Estructura del request inválida");
        }

        if (request.getData().getAttributes().getProductoId() == null) {
            throw new BusinessException("El productoId es obligatorio");
        }

        if (request.getData().getAttributes().getCantidad() == null
                || request.getData().getAttributes().getCantidad() <= 0) {
            throw new BusinessException("La cantidad debe ser mayor a cero");
        }
    }

    private CompraResponseDTO construirRespuesta(CompraEntity compra) {

        // Crear objeto response
        CompraResponseDTO response = new CompraResponseDTO();

        // Crear Data
        CompraResponseDTO.Data data = new CompraResponseDTO.Data();
        data.setId(compra.getId()); // id de la compra

        // Crear Attributes
        CompraResponseDTO.Attributes attributes = new CompraResponseDTO.Attributes();
        attributes.setProductoId(compra.getProductoId());
        attributes.setCantidad(compra.getCantidad());
        attributes.setTotal(compra.getTotal());

        // Asignar attributes a data
        data.setAttributes(attributes);

        // Asignar data a response
        response.setData(data);

        return response;
    }

    private String safeJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            return "No fue posible serializar el objeto";
        }
    }
}