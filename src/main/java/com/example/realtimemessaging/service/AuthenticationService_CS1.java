package com.example.realtimemessaging.service;

import com.example.realtimemessaging.domain.AuthenticatedUser_CS1;
import java.util.Optional;

/**
 * Service responsible for handling user authentication logic.
 */
public interface AuthenticationService_CS1 {

    /**
     * Validates a bearer token and returns the authenticated user details.
     * @param bearerToken The full bearer token string (e.g., "Bearer ...").
     * @return An Optional containing the AuthenticatedUser if the token is valid, otherwise an empty Optional.
     */
    Optional<AuthenticatedUser_CS1> authenticate(String bearerToken);
}
```
src/main/java/com/example/realtimemessaging/service/AuthenticationServiceImpl_CS1.java
```java