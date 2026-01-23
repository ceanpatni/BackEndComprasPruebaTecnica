package com.linktic.compras.dto.request;

public record LoginRequest(
        String username,
        String password
) {}