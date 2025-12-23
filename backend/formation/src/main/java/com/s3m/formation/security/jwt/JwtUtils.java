package com.s3m.formation.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.Map;

@Component
public class JwtUtils {

    @Value("${security.jwt.secret}")
    private String jwtSecret;

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    /* =========================
       CORE
       ========================= */

    public Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean validateToken(String token) {
        try {
            extractClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /* =========================
       CLAIM HELPERS
       ========================= */

    public Integer extractEntrepriseId(String token) {
        return extractClaims(token).get("entrepriseId", Integer.class);
    }

    public String extractRole(String token) {
        return extractClaims(token).get("role", String.class);
    }

    public String extractEmail(String token) {
        return extractClaims(token).getSubject(); // standard JWT usage
    }

    public Integer getEntrepriseId(String token) {
        return extractClaims(token).get("entrepriseId", Integer.class);
    }

    public String getRole(String token) {
        return extractClaims(token).get("role", String.class);
    }

    public String getEmail(String token) {
        return extractClaims(token).get("email", String.class);
    }


    public String generateToken(
            String email,
            Integer entrepriseId,
            String role
    ) {
        return Jwts.builder()
                .setSubject(email)
                .addClaims(Map.of(
                        "entrepriseId", entrepriseId,
                        "role", role,
                        "email", email
                ))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 24h
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

}
