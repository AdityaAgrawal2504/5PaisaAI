package com.omqs.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
/**
 * Custom exception for cases where a requested resource is not found.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException_OMQS1 extends RuntimeException {
    public ResourceNotFoundException_OMQS1(String message) {
        super(message);
    }
}
```
```java
// src/main/java/com/omqs/exception/ValidationException_OMQS1.java