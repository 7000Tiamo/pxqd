package com.training.util;

import com.training.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    private final JwtProperties properties;
    private final byte[] secretKeyBytes;

    public JwtUtil(JwtProperties properties) {
        this.properties = properties;
        this.secretKeyBytes = properties.getSecret().getBytes(StandardCharsets.UTF_8);
    }

    public String generateToken(Long userId) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + properties.getExpiration());
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(SignatureAlgorithm.HS256, secretKeyBytes)
                .compact();
    }

    public Long parse(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(secretKeyBytes)
                    .parseClaimsJws(token)
                    .getBody();
            return Long.valueOf(claims.getSubject());
        } catch (Exception e) {
            return null;
        }
    }
}
