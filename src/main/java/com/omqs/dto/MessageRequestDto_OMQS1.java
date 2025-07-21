package com.omqs.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * DTO representing the request body for enqueuing a message.
 */
@Data
public class MessageRequestDto_OMQS1 {
    @NotNull(message = "senderId must not be empty and must be a valid UUID.")
    private UUID senderId;

    @NotNull(message = "recipientId must not be empty and must be a valid UUID.")
    private UUID recipientId;

    @NotNull(message = "payload must be a non-empty JSON object.")
    private Map<String, Object> payload;

    @NotNull(message = "timestamp must be a valid ISO 8601 date-time string.")
    private Instant timestamp;
}
```
```java
// src/main/java/com/omqs/dto/EnqueueResponseDto_OMQS1.java