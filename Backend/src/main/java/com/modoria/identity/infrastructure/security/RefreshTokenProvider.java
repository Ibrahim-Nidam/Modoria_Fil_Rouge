package com.modoria.identity.infrastructure.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class RefreshTokenProvider {

    private final SecretKey key;
    private final long expirationMs;

    public RefreshTokenProvider(
            @Value("${jwt.refresh-secret}") String refreshSecretBase64,
            @Value("${jwt.refresh-expiration-ms}") long refreshExpirationMs) {

        this.key = Keys.hmacShaKeyFor(java.util.Base64.getDecoder().decode(refreshSecretBase64));
        this.expirationMs = refreshExpirationMs;
    }

    public String generateRefreshToken(String email) {
        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(key)
                .compact();
    }

    public String getEmail(String token) {
        return parseClaims(token).getSubject();
    }

    public boolean validate(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
