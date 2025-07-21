package com.omqs.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import java.util.Map;
import java.util.UUID;

/**
 * DTO for the request body to acknowledge and remove messages.
 */
@Data
public class MessageAcknowledgementDto_OMQS1 {
    @NotEmpty(message = "messageIds object must not be empty.")
    private Map<UUID, Boolean> messageIds;
}
```
```java
// src/main/java/com/omqs/dto/ErrorResponseDto_OMQS1.java