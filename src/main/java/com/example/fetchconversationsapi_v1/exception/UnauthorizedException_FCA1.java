package com.example.fetchconversationsapi_v1.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class UnauthorizedException_FCA1 extends RuntimeException {
    public UnauthorizedException_FCA1(String message) {
        super(message);
    }
}
```
```java
// Global Exception Handler
// File: GlobalExceptionHandler_FCA1.java