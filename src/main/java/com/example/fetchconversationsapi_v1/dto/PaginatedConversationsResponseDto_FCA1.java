package com.example.fetchconversationsapi_v1.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class PaginatedConversationsResponseDto_FCA1 {
    private List<ConversationSummaryDto_FCA1> data;
    private PaginationInfoDto_FCA1 pagination;
}
```
```java
// DTO to capture and validate request parameters
// File: FetchConversationsRequestDto_FCA1.java