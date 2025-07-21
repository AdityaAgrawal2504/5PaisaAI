package com.example.fetchconversationsapi_v1.dto;

import lombok.Builder;
import lombok.Data;
import java.util.UUID;

@Data
@Builder
public class ParticipantDto_FCA1 {
    private UUID userId;
    private String displayName;
    private String avatarUrl;
}
```
```java
// DTO for Conversation Summary
// File: ConversationSummaryDto_FCA1.java