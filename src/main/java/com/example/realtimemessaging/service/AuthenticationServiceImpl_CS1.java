package com.example.realtimemessaging.service;

import com.example.realtimemessaging.domain.AuthenticatedUser_CS1;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Optional;

/**
 * Mock implementation of the AuthenticationService for validating JWTs.
 * In a real application, this would use a public key to verify the token signature.
 */
@Service
public class AuthenticationServiceImpl_CS1 implements AuthenticationService_CS1 {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationServiceImpl_CS1.class);
    // IMPORTANT: In a real app, this key should be loaded from a secure vault and be much more complex.
    private static final SecretKey MOCK_SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    /**
     * Parses and validates a JWT bearer token.
     */
    @Override
    public Optional<AuthenticatedUser_CS1> authenticate(String bearerToken) {
        long startTime = System.currentTimeMillis();
        if (bearerToken == null || !bearerToken.startsWith("Bearer ")) {
            logger.warn("Authentication failed: Token is missing or doesn't start with 'Bearer '.");
            logExecutionTime(startTime);
            return Optional.empty();
        }

        String token = bearerToken.substring(7);

        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(MOCK_SECRET_KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String userId = claims.getSubject();
            String username = claims.get("username", String.class);

            if (userId == null || username == null) {
                logger.warn("Authentication failed: Token is missing required claims (sub, username).");
                logExecutionTime(startTime);
                return Optional.empty();
            }

            logger.info("Successfully authenticated user {}", userId);
            logExecutionTime(startTime);
            return Optional.of(new AuthenticatedUser_CS1(userId, username));
        } catch (Exception e) {
            logger.error("Authentication failed: Token validation error for token '{}'. Reason: {}", token, e.getMessage());
            logExecutionTime(startTime);
            return Optional.empty();
        }
    }

    private void logExecutionTime(long startTime) {
        long endTime = System.currentTimeMillis();
        logger.debug("Execution time: {} ms", (endTime - startTime));
    }
}
```
src/main/java/com/example/realtimemessaging/util/IdGenerator_CS1.java
<ctrl60><ctrl62><ctrl61>package com.example.realtimemessaging.util;

import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Utility for generating unique identifiers.
 */
@Component
public class IdGenerator_CS1 {

    /**
     * Generates a new UUID v4 string.
     * @return A unique identifier.
     */
    public String newId() {
        return UUID.randomUUID().toString();
    }
}
```
src/main/java/com/example/realtimemessaging/util/TimestampConverter_CS1.java
```java