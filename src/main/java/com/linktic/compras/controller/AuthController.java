package com.linktic.compras.controller;

import com.linktic.compras.dto.request.LoginRequest;
import com.linktic.compras.entity.UsuarioEntity;
import com.linktic.compras.repository.UsuarioRepository;
import com.linktic.compras.security.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UsuarioRepository repo;
    private final JwtService jwt;

    public AuthController(UsuarioRepository repo, JwtService jwt){
        this.repo = repo;
        this.jwt = jwt;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req){
        UsuarioEntity u = repo.findByUsername(req.username())
                .orElseThrow();

        if(!u.getPassword().equals(req.password()))
            return ResponseEntity.status(401).build();

        String token = jwt.generateToken(u.getUsername(),u.getRole(),30);
        String refresh = jwt.generateToken(u.getUsername(),u.getRole(),120);

        return ResponseEntity.ok(Map.of(
                "access_token", token,
                "refresh_token", refresh
        ));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody Map<String,String> body){
        String refreshToken = body.get("refresh_token");
        // En prueba técnica asumimos válido si firma correcta
        Claims c = Jwts.parser()
                .verifyWith(jwt.getPublicKey())
                .build()
                .parseSignedClaims(refreshToken)
                .getPayload();

        String newToken = jwt.generateToken(
                c.getSubject(),
                c.get("role").toString(),
                30
        );

        return ResponseEntity.ok(Map.of("access_token",newToken));
    }
}
