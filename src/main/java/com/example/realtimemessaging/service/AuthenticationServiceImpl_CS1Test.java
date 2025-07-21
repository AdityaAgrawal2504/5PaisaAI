package com.example.realtimemessaging.service;

import com.example.realtimemessaging.domain.AuthenticatedUser_CS1;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class AuthenticationServiceImpl_CS1Test {

    private AuthenticationServiceImpl_CS1 authenticationService;
    private static final SecretKey TEST_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    @BeforeEach
    void setUp() {
        // We test the real implementation
        authenticationService = new AuthenticationServiceImpl_CS1();
    }

    private String generateTestToken(String userId, String username) {
        return Jwts.builder()
                .setSubject(userId)
                .claim("username", username)
                .signWith(TEST_KEY)
                .compact();
    }

    // This test is tricky as the service uses its own private key.
    // For a real test, we'd inject the key or a mock parser.
    // Here, we'll test the logic paths (null, malformed, etc.) rather than crypto.
    @Test
    void authenticate_withValidFormatButUnknownKey_shouldFail() {
        // This token is signed with a different key than the service uses.
        String token = generateTestToken("user-123", "testuser");
        Optional<AuthenticatedUser_CS1> result = authenticationService.authenticate("Bearer " + token);
        assertFalse(result.isPresent());
    }

    @Test
    void authenticate_withNullToken_shouldReturnEmpty() {
        Optional<AuthenticatedUser_CS1> result = authenticationService.authenticate(null);
        assertFalse(result.isPresent());
    }

    @Test
    void authenticate_withNonBearerToken_shouldReturnEmpty() {
        Optional<AuthenticatedUser_CS1> result = authenticationService.authenticate("Basic some-token");
        assertFalse(result.isPresent());
    }

    @Test
    void authenticate_withMalformedToken_shouldReturnEmpty() {
        Optional<AuthenticatedUser_CS1> result = authenticationService.authenticate("Bearer malformed.token.string");
        assertFalse(result.isPresent());
    }

    @Test
    void authenticate_withEmptyToken_shouldReturnEmpty() {
        Optional<AuthenticatedUser_CS1> result = authenticationService.authenticate("Bearer ");
        assertFalse(result.isPresent());
    }
}
```
src/test/java/com/example/realtimemessaging/manager/SessionManager_CS1Test.java
```java