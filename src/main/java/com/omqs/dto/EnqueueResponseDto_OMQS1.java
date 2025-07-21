package com.omqs.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

/**
 * DTO for the 202 Accepted response after enqueuing a message.
 */
@Data
@AllArgsConstructor
public class EnqueueResponseDto_OMQS1 {
    private UUID messageId;
    private Instant enqueuedAt;
}
```
```java
// src/main/java/com/omqs/dto/MessageAcknowledgementDto_OMQS1.java