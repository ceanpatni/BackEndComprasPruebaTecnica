package com.linktic.compras.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.linktic.compras.config.ProductoClient;
import com.linktic.compras.dto.request.InventarioRequestDTO;
import com.linktic.compras.dto.response.InventarioResponseDTO;
import com.linktic.compras.dto.response.ProductoResponseDTO;
import com.linktic.compras.entity.InventarioEntity;
import com.linktic.compras.exception.BusinessException;
import com.linktic.compras.repository.InventarioRepository;
import com.linktic.compras.service.interfaces.InventarioService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class InventarioServiceImpl implements InventarioService {
    private static final Logger logger = LoggerFactory.getLogger(InventarioServiceImpl.class);

    private final InventarioRepository inventarioRepository;
    private final ProductoClient productoClient;
    private final ObjectMapper objectMapper;

    public InventarioServiceImpl(InventarioRepository inventarioRepository, ProductoClient productoClient, ObjectMapper objectMapper) {
        this.inventarioRepository = inventarioRepository;
        this.productoClient = productoClient;
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional
    public InventarioResponseDTO consultarCantidad(Long productoId, String token) {
        // validar existencia del producto en BackendProductos
        ProductoResponseDTO productoResponse =
                productoClient.obtenerProducto(
                        productoId,
                        token
                );
        logger.info("ProductoResponse recibido: {}", productoResponse);
        if (productoResponse.getData() == null
                || productoResponse.getData().getId() == null
                || (productoResponse.getData().getAttributes() != null
                && "El producto no existe".equals(productoResponse.getData().getAttributes().getError()))) {
            throw new BusinessException("El producto no existe");
        }




        InventarioEntity inventario = inventarioRepository
                .findByProductoId(productoId)
                .orElseThrow(() -> new BusinessException("Producto no registrado en inventario"));

        InventarioResponseDTO response = new InventarioResponseDTO();
        InventarioResponseDTO.Data data = new InventarioResponseDTO.Data();
        data.setType("inventario");
        data.setId(inventario.getId());

        InventarioResponseDTO.Attributes attributes = new InventarioResponseDTO.Attributes();
        attributes.setProductoId(inventario.getProductoId());
        attributes.setCantidadDisponible(inventario.getCantidad());

        data.setAttributes(attributes);
        response.setData(data);

        return response;
    }

    @Override
    @Transactional
    public InventarioResponseDTO actualizarCantidad(InventarioRequestDTO request, String token, boolean esSuma) {
        InventarioResponseDTO responseDTO = new InventarioResponseDTO();
        InventarioResponseDTO.Data data = new InventarioResponseDTO.Data();
        data.setType("inventario");

        InventarioResponseDTO.Attributes attributes = new InventarioResponseDTO.Attributes();

        try {
            Long productoId = request.getData().getAttributes().getProductoId();
            Integer cantidad = request.getData().getAttributes().getCantidad();

            ProductoResponseDTO productoResponse =
                    productoClient.obtenerProducto(
                            productoId,
                            token
                    );
            logger.info("ProductoResponse recibido: {}", productoResponse);
            if (productoResponse.getData() == null
                    || productoResponse.getData().getId() == null
                    || (productoResponse.getData().getAttributes() != null
                    && "El producto no existe".equals(productoResponse.getData().getAttributes().getError()))) {
                throw new BusinessException("El producto no existe");
            }


            InventarioEntity inventario = inventarioRepository
                    .findByProductoId(productoId)
                    .orElseGet(() -> {
                        InventarioEntity nuevo = new InventarioEntity();
                        nuevo.setProductoId(productoId);
                        nuevo.setCantidad(0);
                        return nuevo;
                    });

            // actualizar stock
            int nuevaCantidad;
            if (esSuma) {
                // sumar cantidad
                nuevaCantidad = inventario.getCantidad() + cantidad;
            } else {
                // restar cantidad (compra)
                nuevaCantidad = inventario.getCantidad() - cantidad;
                if (nuevaCantidad < 0) {
                    throw new BusinessException("Stock insuficiente");
                }
            }

            inventario.setCantidad(nuevaCantidad);
            InventarioEntity guardado = inventarioRepository.save(inventario);

            data.setId(guardado.getId());
            attributes.setProductoId(guardado.getProductoId());
            attributes.setCantidadDisponible(guardado.getCantidad());
            data.setAttributes(attributes);
            responseDTO.setData(data);

            log.info("Inventario actualizado: {}", objectMapper.writeValueAsString(responseDTO));

            return responseDTO;

        } catch (BusinessException ex) {
            attributes.setError(ex.getMessage());
            data.setAttributes(attributes);
            responseDTO.setData(data);
            return responseDTO;
        } catch (Exception ex) {
            attributes.setError("Error interno del servidor");
            data.setAttributes(attributes);
            responseDTO.setData(data);
            log.error("Error actualizando inventario", ex);
            return responseDTO;
        }
    }
    @Override
    @Transactional
    public InventarioResponseDTO crearInventario(InventarioRequestDTO request, String token) throws JsonProcessingException {
        InventarioResponseDTO responseDTO = new InventarioResponseDTO();
        InventarioResponseDTO.Data data = new InventarioResponseDTO.Data();
        data.setType("inventario");

        InventarioResponseDTO.Attributes attributes = new InventarioResponseDTO.Attributes();

        try {
            Long productoId = request.getData().getAttributes().getProductoId();
            Integer cantidad = request.getData().getAttributes().getCantidad();

            ProductoResponseDTO productoResponse =
                    productoClient.obtenerProducto(
                            productoId,
                            token
                    );
            logger.info("ProductoResponse recibido: {}", productoResponse);
            if (productoResponse.getData() == null
                    || productoResponse.getData().getId() == null
                    || (productoResponse.getData().getAttributes() != null
                    && "El producto no existe".equals(productoResponse.getData().getAttributes().getError()))) {
                throw new BusinessException("El producto no existe");
            }

            // Verificar si ya existe inventario
            InventarioEntity inventarioExistente = inventarioRepository
                    .findByProductoId(productoId)
                    .orElse(null);

            if (inventarioExistente != null) {
                throw new BusinessException("El inventario para este producto ya existe");
            }

            // Crear inventario
            InventarioEntity inventario = new InventarioEntity();
            inventario.setProductoId(productoId);
            inventario.setCantidad(cantidad);

            InventarioEntity guardado = inventarioRepository.save(inventario);

            data.setId(guardado.getId());
            attributes.setProductoId(guardado.getProductoId());
            attributes.setCantidadDisponible(guardado.getCantidad());
            data.setAttributes(attributes);
            responseDTO.setData(data);

            return responseDTO;

        } catch (BusinessException ex) {
            attributes.setError(ex.getMessage());
            data.setAttributes(attributes);
            responseDTO.setData(data);
            return responseDTO;
        } catch (Exception ex) {
            attributes.setError("Error interno del servidor");
            data.setAttributes(attributes);
            responseDTO.setData(data);
            return responseDTO;
        }
    }
}
