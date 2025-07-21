package com.example.logininitiation.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Custom exception for failed authentication attempts (401 Unauthorized).
 */
@Getter
@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class AuthenticationFailedExceptionLIA_9371 extends RuntimeException {
    private final String message;

    public AuthenticationFailedExceptionLIA_9371(String message) {
        super(message);
        this.message = message;
    }
}
```
src/main/java/com/example/logininitiation/exception/OtpServiceExceptionLIA_9371.java
```java