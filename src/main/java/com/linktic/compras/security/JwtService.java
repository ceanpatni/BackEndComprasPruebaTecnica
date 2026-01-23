package com.linktic.compras.security;

import com.linktic.compras.config.PemUtils;
import io.jsonwebtoken.Jwts;
import org.springframework.core.io.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.stereotype.Service;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Service
public class JwtService {

    private final RSAPrivateKey privateKey;
    private final RSAPublicKey publicKey;

    public JwtService(
            @Value("${security.jwt.private-key-path}") Resource privatePem,
            @Value("${security.jwt.public-key-path}") Resource publicPem
    ) throws Exception {
        this.privateKey = PemUtils.readPrivateKey(privatePem.getInputStream());
        this.publicKey = PemUtils.readPublicKey(publicPem.getInputStream());
    }

    public String generateToken(String username, String role, int minutes) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(username)
                .claim("role", role)
                .issuer("linktic-auth")
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(minutes, ChronoUnit.MINUTES)))
                .signWith(privateKey)
                .compact();
    }

    public RSAPublicKey getPublicKey() {
        return publicKey;
    }
}
