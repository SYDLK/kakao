package com.example.demo.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.issuer}")
    private String issuer;

    // 1시간짜리 Access Token
    public String generateAccessToken(String email) {
        return generateToken(email, 60 * 60 * 1000); // 1시간
    }

    // 7일짜리 Refresh Token
    public String generateRefreshToken(String email) {
        return generateToken(email, 7 * 24 * 60 * 60 * 1000); // 7일
    }

    private String generateToken(String email, long expiryMillis) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expiryMillis);

        return Jwts.builder()
                .setSubject(email)
                .setIssuer(issuer)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(Keys.hmacShaKeyFor(secret.getBytes()), SignatureAlgorithm.HS256)
                .compact();
    }
}
