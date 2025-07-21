package com.example.fetchconversationsapi_v1.security;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class SecurityUtil_FCA1 {
    /**
     * Retrieves the UUID of the currently authenticated user.
     * @return An Optional containing the user's UUID if authenticated, otherwise empty.
     */
    public Optional<UUID> getCurrentUserId() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .map(auth -> {
                    if (auth.getPrincipal() instanceof UserPrincipal_FCA1) {
                        return ((UserPrincipal_FCA1) auth.getPrincipal()).getId();
                    }
                    return null;
                });
    }
}
```
```java
// Base entity with common fields like ID, created, updated timestamps.
// File: BaseEntity_FCA1.java