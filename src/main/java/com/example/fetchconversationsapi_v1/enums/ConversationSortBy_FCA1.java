package com.example.fetchconversationsapi_v1.enums;

import lombok.Getter;

@Getter
public enum ConversationSortBy_FCA1 {
    LAST_MESSAGE_TIME("updatedAt"), // Mapped to entity field
    CREATION_TIME("createdAt"); // Mapped to entity field

    private final String entityField;

    ConversationSortBy_FCA1(String entityField) {
        this.entityField = entityField;
    }
}
```
```java
// Sort Order Enum
// File: SortOrder_FCA1.java