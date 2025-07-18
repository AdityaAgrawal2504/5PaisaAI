package com.example.userregistration.dto;

import com.example.userregistration.enums.ErrorCode;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    private ErrorCode errorCode;
    private String message;
    private List<ErrorDetail> details;

    @Data
    @AllArgsConstructor
    public static class ErrorDetail {
        private String field;
        private String issue;
    }
}
```
// src/main/java/com/example/userregistration/entity/User.java
```java