package com.example.loginverification.model;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode_LVA1 {
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "One or more input fields failed validation."),
    INVALID_OTP(HttpStatus.UNAUTHORIZED, "INVALID_OTP", "The One-Time Password provided is incorrect."),
    OTP_EXPIRED(HttpStatus.UNAUTHORIZED, "OTP_EXPIRED", "The One-Time Password has expired."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", "The specified phone number was not found or has no pending login."),
    TOO_MANY_ATTEMPTS(HttpStatus.TOO_MANY_REQUESTS, "TOO_MANY_ATTEMPTS", "Too many verification attempts. Please try again later."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR", "An internal server error occurred.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    ErrorCode_LVA1(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
```
src/main/java/com/example/loginverification/exception/ApiException_LVA1.java
```java