package com.example.logininitiation.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Custom exception for failures related to the downstream OTP service (500 Internal Server Error).
 */
@Getter
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class OtpServiceExceptionLIA_9371 extends RuntimeException {
    private final String message;

    public OtpServiceExceptionLIA_9371(String message) {
        super(message);
        this.message = message;
    }
}
```
src/main/java/com/example/logininitiation/exception/GlobalExceptionHandlerLIA_9371.java
```java