package com.example.userregistration.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(HttpStatus.CONFLICT)
public class UserAlreadyExistsException extends RuntimeException {
    private final String phoneNumber;

    public UserAlreadyExistsException(String phoneNumber) {
        super("A user with phone number '" + phoneNumber + "' already exists.");
        this.phoneNumber = phoneNumber;
    }
}
```
// src/main/java/com/example/userregistration/exception/GlobalExceptionHandler.java
```java