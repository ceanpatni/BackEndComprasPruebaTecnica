package com.linktic.compras.repository;

import com.linktic.compras.entity.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<UsuarioEntity,Long> {
    Optional<UsuarioEntity> findByUsername(String username);
}