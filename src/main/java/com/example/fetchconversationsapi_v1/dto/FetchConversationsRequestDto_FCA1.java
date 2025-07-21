package com.example.fetchconversationsapi_v1.dto;

import com.example.fetchconversationsapi_v1.enums.ConversationSortBy_FCA1;
import com.example.fetchconversationsapi_v1.enums.SortOrder_FCA1;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FetchConversationsRequestDto_FCA1 {

    @Min(value = 1, message = "Page must be a positive integer.")
    private int page = 1;

    @Min(value = 1, message = "Page size must be at least 1.")
    @Max(value = 100, message = "Page size must not exceed 100.")
    private int pageSize = 20;

    private ConversationSortBy_FCA1 sortBy = ConversationSortBy_FCA1.LAST_MESSAGE_TIME;

    private SortOrder_FCA1 sortOrder = SortOrder_FCA1.DESC;

    @Nullable
    private Boolean seen;

    @Nullable
    @Size(max = 255, message = "Search query must not exceed 255 characters.")
    private String searchQuery;
}
```
```java
// Security Principal representing the authenticated user
// File: UserPrincipal_FCA1.java