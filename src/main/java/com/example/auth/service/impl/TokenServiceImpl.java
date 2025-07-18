package com.example.auth.service.impl;

import com.example.auth.service.ITokenService;
import com.example.auth.util.logging.Loggable;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of ITokenService for generating JWTs.
 */
@Service
public class TokenServiceImpl implements ITokenService {

    @Value("${app.jwt.otp-secret}")
    private String jwtSecret;

    @Value("${app.jwt.otp-expiration-ms}")
    private long jwtExpirationMs;

    /**
     * Generates a short-lived JWT specifically for the OTP verification step.
     * The token includes the user ID and a specific 'scope' claim.
     * @param userId The ID of the user.
     * @return A signed JWT string.
     */
    @Override
    @Loggable
    public String generateOtpSessionToken(String userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("scope", "otp_verify");

        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userId)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
}


// =================================================================
// CONTROLLER
// =================================================================
