package com.example.logininitiation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for the successful login initiation response.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginInitiationResponseLIA_9371 {

    /**
     * A confirmation message for the user.
     */
    private String message;

    /**
     * The unique token to correlate this initiation with a subsequent OTP verification.
     */
    private String correlationId;
}
```
src/main/java/com/example/logininitiation/exception/AuthenticationFailedExceptionLIA_9371.java
```java