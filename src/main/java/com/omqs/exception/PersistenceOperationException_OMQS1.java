package com.omqs.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Custom exception for failures during persistence operations.
 */
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class PersistenceOperationException_OMQS1 extends RuntimeException {
    public PersistenceOperationException_OMQS1(String message, Throwable cause) {
        super(message, cause);
    }
}
```
```java
// src/main/java/com/omqs/exception/ResourceNotFoundException_OMQS1.java