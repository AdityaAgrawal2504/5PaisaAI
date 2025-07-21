package com.example.fetchconversationsapi_v1.dto;

import lombok.Builder;
import lombok.Data;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class ConversationSummaryDto_FCA1 {
    private UUID id;
    private String title;
    private List<ParticipantDto_FCA1> participants;
    private MessageSnippetDto_FCA1 lastMessage;
    private int unreadCount;
    private boolean isSeen;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
```
```java
// DTO for Pagination Information
// File: PaginationInfoDto_FCA1.java