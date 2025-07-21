package com.example.fetchconversationsapi_v1.dto;

import lombok.Builder;
import lombok.Data;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
public class MessageSnippetDto_FCA1 {
    private UUID id;
    private UUID senderId;
    private String text;
    private OffsetDateTime timestamp;
}
```
```java
// DTO for Participant
// File: ParticipantDto_FCA1.java