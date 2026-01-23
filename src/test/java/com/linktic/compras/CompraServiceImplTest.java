package com.linktic.compras;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.linktic.compras.config.ProductoClient;
import com.linktic.compras.dto.request.CompraRequestDTO;
import com.linktic.compras.dto.request.InventarioRequestDTO;
import com.linktic.compras.dto.response.CompraResponseDTO;
import com.linktic.compras.dto.response.InventarioResponseDTO;
import com.linktic.compras.dto.response.ProductoResponseDTO;
import com.linktic.compras.entity.CompraEntity;
import com.linktic.compras.entity.InventarioEntity;
import com.linktic.compras.repository.AuditoriaRepository;
import com.linktic.compras.repository.CompraRepository;
import com.linktic.compras.repository.InventarioRepository;
import com.linktic.compras.service.impl.CompraServiceImpl;
import com.linktic.compras.service.interfaces.InventarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompraServiceImplTest {

    private CompraServiceImpl compraService;

    @Mock
    private InventarioRepository inventarioRepository;

    @Mock
    private CompraRepository compraRepository;

    @Mock
    private AuditoriaRepository auditoriaRepository;

    @Mock
    private ProductoClient productoClient;

    @Mock
    private InventarioService inventarioService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        objectMapper = new ObjectMapper();
        compraService = new CompraServiceImpl(
                compraRepository,
                auditoriaRepository,
                inventarioRepository,
                objectMapper,
                productoClient,
                inventarioService
        );
    }

    @Test
    void realizarCompraExitosa() throws Exception {
        InventarioEntity inventario = new InventarioEntity();
        inventario.setProductoId(1L);
        inventario.setCantidad(10);
        when(inventarioRepository.findByProductoId(1L)).thenReturn(Optional.of(inventario));

        ProductoResponseDTO productoResponse = new ProductoResponseDTO();
        ProductoResponseDTO.Data data = new ProductoResponseDTO.Data();
        data.setId(1L);
        ProductoResponseDTO.Attributes attr = new ProductoResponseDTO.Attributes();
        attr.setNombre("Laptop");
        attr.setPrecio(BigDecimal.valueOf(1000));
        data.setAttributes(attr);
        productoResponse.setData(data);
        when(productoClient.obtenerProducto(anyLong(), anyString())).thenReturn(productoResponse);

        InventarioResponseDTO invResponse = new InventarioResponseDTO();
        InventarioResponseDTO.Data dataInv = new InventarioResponseDTO.Data();
        InventarioResponseDTO.Attributes attrInv = new InventarioResponseDTO.Attributes();
        attrInv.setCantidadDisponible(8);
        dataInv.setAttributes(attrInv);
        invResponse.setData(dataInv);
        when(inventarioService.actualizarCantidad(any(), anyString(), anyBoolean())).thenReturn(invResponse);

        when(compraRepository.save(any())).thenAnswer(invocation -> {
            CompraEntity c = invocation.getArgument(0);
            c.setId(1L);
            return c;
        });

        CompraRequestDTO request = buildRequest(1L, 2);
        CompraResponseDTO response = compraService.realizarCompra(request, "token-falso");

        assertNotNull(response);
        assertEquals(1L, response.getData().getId());
        assertEquals(2, response.getData().getAttributes().getCantidad());
        assertEquals(BigDecimal.valueOf(2000), response.getData().getAttributes().getTotal());
        assertNull(response.getData().getAttributes().getError());

        verify(compraRepository).save(any());
        verify(auditoriaRepository).save(any());
        verify(inventarioService).actualizarCantidad(any(), anyString(), anyBoolean());
    }

    @Test
    void errorInventarioInsuficiente() throws Exception {
        InventarioEntity inventario = new InventarioEntity();
        inventario.setProductoId(1L);
        inventario.setCantidad(1); // solo 1 disponible
        when(inventarioRepository.findByProductoId(1L)).thenReturn(Optional.of(inventario));

        ProductoResponseDTO productoResponse = new ProductoResponseDTO();
        ProductoResponseDTO.Data data = new ProductoResponseDTO.Data();
        data.setId(1L);
        ProductoResponseDTO.Attributes attr = new ProductoResponseDTO.Attributes();
        attr.setNombre("Laptop");
        attr.setPrecio(BigDecimal.valueOf(1000));
        data.setAttributes(attr);
        productoResponse.setData(data);
        when(productoClient.obtenerProducto(anyLong(), anyString())).thenReturn(productoResponse);

        CompraRequestDTO request = buildRequest(1L, 5); // solicita más de lo disponible
        CompraResponseDTO response = compraService.realizarCompra(request, "token-falso");

        assertNotNull(response);
        assertEquals("Stock insuficiente", response.getData().getAttributes().getError());
    }

    @Test
    void errorProductoNoExiste() throws Exception {
        ProductoResponseDTO productoResponse = new ProductoResponseDTO();
        ProductoResponseDTO.Data data = new ProductoResponseDTO.Data();
        data.setAttributes(new ProductoResponseDTO.Attributes());
        data.getAttributes().setError("El producto no existe");
        productoResponse.setData(data);

        when(productoClient.obtenerProducto(anyLong(), anyString())).thenReturn(productoResponse);

        CompraRequestDTO request = buildRequest(999L, 1);
        CompraResponseDTO response = compraService.realizarCompra(request, "token-falso");

        assertNotNull(response);
        assertEquals("El producto no existe", response.getData().getAttributes().getError());
    }

    private CompraRequestDTO buildRequest(Long productoId, int cantidad) {
        CompraRequestDTO request = new CompraRequestDTO();
        CompraRequestDTO.Data data = new CompraRequestDTO.Data();
        CompraRequestDTO.Attributes attr = new CompraRequestDTO.Attributes();
        attr.setProductoId(productoId);
        attr.setCantidad(cantidad);
        data.setAttributes(attr);
        request.setData(data);
        return request;
    }
}
