package com.omqs.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * DTO for the Message data structure used in API responses.
 */
@Data
@NoArgsConstructor
public class MessageDto_OMQS1 {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private UUID messageId;

    @NotNull
    private UUID senderId;

    @NotNull
    private UUID recipientId;

    @NotNull
    private Map<String, Object> payload;

    @NotNull
    private Instant timestamp;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Instant enqueuedAt;
}
```
```java
// src/main/java/com/omqs/dto/MessageRequestDto_OMQS1.java