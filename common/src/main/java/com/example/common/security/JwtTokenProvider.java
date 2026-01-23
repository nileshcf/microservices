package com.example.common.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

@Component
public class JwtTokenProvider {

    private final SecretKey key;

    public JwtTokenProvider() {
        // Ensure JwtConstants.SECRET is at least 32 characters long!
        this.key = Keys.hmacShaKeyFor(JwtConstants.SECRET.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(String username, Map<String, Object> claims) {
        return Jwts.builder()
                .setClaims(claims) // Note: .setClaims in newer JJWT versions overwrites previous. Use .addClaims if needed.
                .setSubject(username) // Subject must be set AFTER claims if using setClaims
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
                .signWith(key)
                .compact();
    }
}