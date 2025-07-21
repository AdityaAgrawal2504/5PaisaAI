package com.omqs.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.omqs.constants.ErrorCode_OMQS1;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Map;

/**
 * Standardized DTO for error responses.
 */
@Data
@RequiredArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponseDto_OMQS1 {
    @NonNull
    private ErrorCode_OMQS1 errorCode;
    @NonNull
    private String errorMessage;
    private Map<String, String> details;
}
```
```java
// src/main/java/com/omqs/exception/PersistenceOperationException_OMQS1.java