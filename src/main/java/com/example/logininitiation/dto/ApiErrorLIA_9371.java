package com.example.logininitiation.dto;

import com.example.logininitiation.enums.ErrorCodeLIA_9371;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for standardized API error responses.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiErrorLIA_9371 {

    /**
     * A unique, machine-readable code for the specific error.
     */
    private ErrorCodeLIA_9371 errorCode;

    /**
     * A developer-friendly message detailing the error.
     */
    private String message;
}
```
src/main/java/com/example/logininitiation/dto/LoginInitiationRequestLIA_9371.java
```java