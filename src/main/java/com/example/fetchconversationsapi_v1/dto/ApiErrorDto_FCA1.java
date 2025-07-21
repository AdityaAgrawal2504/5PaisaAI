package com.example.fetchconversationsapi_v1.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiErrorDto_FCA1 {
    private String errorCode;
    private String message;
    private Map<String, String> details;
}
```
```java
// DTO for Message Snippet
// File: MessageSnippetDto_FCA1.java