package com.example.fetchconversationsapi_v1.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaginationInfoDto_FCA1 {
    private int currentPage;
    private int pageSize;
    private int totalPages;
    private long totalItems;
}
```
```java
// DTO for the final paginated response
// File: PaginatedConversationsResponseDto_FCA1.java