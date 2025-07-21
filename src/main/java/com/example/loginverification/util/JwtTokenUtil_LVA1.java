package com.example.loginverification.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtTokenUtil_LVA1 {

    private final SecretKey secretKey;
    private final long expirationSeconds;

    public JwtTokenUtil_LVA1(@Value("${jwt.secret}") String secret,
                           @Value("${jwt.expiration.seconds}") long expirationSeconds) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationSeconds = expirationSeconds;
    }

    /**
     * Generates a JWT for the given phone number.
     * @param phoneNumber The subject for which the token is generated.
     * @return A signed JWT string.
     */
    public String generateToken(String phoneNumber) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationSeconds * 1000);

        return Jwts.builder()
                .subject(phoneNumber)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(secretKey)
                .compact();
    }
}
```
src/main/java/com/example/loginverification/service/OtpStoreService_LVA1.java
```java