package com.omqs.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
/**
 * Custom exception for business logic validation failures.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ValidationException_OMQS1 extends RuntimeException {
    public ValidationException_OMQS1(String message) {
        super(message);
    }
}
```
```java
// src/main/java/com/omqs/exception/GlobalExceptionHandler_OMQS1.java