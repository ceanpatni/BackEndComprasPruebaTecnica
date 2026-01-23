package com.linktic.compras.repository;

import com.linktic.compras.entity.InventarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InventarioRepository extends JpaRepository<InventarioEntity, Long> {
    Optional<InventarioEntity> findByProductoId(Long productoId);
}