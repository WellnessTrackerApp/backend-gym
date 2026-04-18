package com.gymtracker.app.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Component
public class JwtService {
    private final Clock clock;
    private final SecretKey signingKey;

    public JwtService(Clock clock, @Value("${jwt.secret.key}") String secretKey) {
        this.clock = clock;
        this.signingKey = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(String username, String userId) {
        Instant now = Instant.now(clock);
        Instant expiryDate = now.plus(1, ChronoUnit.DAYS);

        return Jwts.builder()
                .claims()
                .add("username", username)
                .subject(userId)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiryDate))
                .and()
                .signWith(signingKey)
                .compact();
    }

    public String extractSubject(String jwt) {
        Claims claims = Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(jwt)
                .getPayload();

        return claims.getSubject();
    }
}
