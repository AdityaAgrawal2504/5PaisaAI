package com.example.userregistration.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRegistrationResponse {
    private UUID userId;
    private String message;
    private Instant createdAt;
}
```
// src/main/java/com/example/userregistration/dto/ErrorResponse.java
```java