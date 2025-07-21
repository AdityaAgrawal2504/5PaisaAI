package com.example.realtimemessaging.domain;

/**
 * A simple domain object to represent an authenticated user's details.
 * This is typically stored in the gRPC context after successful authentication.
 */
public class AuthenticatedUser_CS1 {
    private final String userId;
    private final String username;

    public AuthenticatedUser_CS1(String userId, String username) {
        this.userId = userId;
        this.username = username;
    }

    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }
}
```
src/main/java/com/example/realtimemessaging/enums/StreamErrorCode_CS1.java
```java