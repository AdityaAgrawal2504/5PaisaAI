package com.example.fetchconversationsapi_v1.exception;

import com.example.fetchconversationsapi_v1.dto.ApiErrorDto_FCA1;
import com.example.fetchconversationsapi_v1.logging.AppLogger_FCA1;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler_FCA1 {
    
    private final AppLogger_FCA1 logger;

    /**
     * Handles validation failures for request body DTOs annotated with @Valid.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorDto_FCA1> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));

        ApiErrorDto_FCA1 apiError = ApiErrorDto_FCA1.builder()
                .errorCode("INVALID_PARAMETER")
                .message("One or more query parameters are invalid.")
                .details(errors)
                .build();
        
        logger.warn("Validation failed: {}", apiError);
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }
    
    /**
     * Handles validation failures for individual controller method parameters.
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiErrorDto_FCA1> handleConstraintViolation(ConstraintViolationException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getConstraintViolations().forEach(violation -> {
            String field = violation.getPropertyPath().toString();
            // Clean up the field name
            field = field.substring(field.lastIndexOf('.') + 1);
            errors.put(field, violation.getMessage());
        });

        ApiErrorDto_FCA1 apiError = ApiErrorDto_FCA1.builder()
                .errorCode("INVALID_PARAMETER")
                .message("One or more query parameters are invalid.")
                .details(errors)
                .build();

        logger.warn("Constraint violation: {}", apiError);
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles errors when a parameter cannot be converted to the required type (e.g., string to enum).
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiErrorDto_FCA1> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String message = String.format("Parameter '%s' has an invalid value '%s'.", ex.getName(), ex.getValue());
        ApiErrorDto_FCA1 apiError = ApiErrorDto_FCA1.builder()
                .errorCode("INVALID_PARAMETER")
                .message(message)
                .build();

        logger.warn("Parameter type mismatch: {}", message);
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles unauthorized access exceptions.
     */
    @ExceptionHandler(UnauthorizedException_FCA1.class)
    public ResponseEntity<ApiErrorDto_FCA1> handleUnauthorizedException(UnauthorizedException_FCA1 ex) {
        ApiErrorDto_FCA1 apiError = ApiErrorDto_FCA1.builder()
                .errorCode("UNAUTHORIZED")
                .message(ex.getMessage())
                .build();
        
        logger.warn("Unauthorized access attempt: {}", ex.getMessage());
        return new ResponseEntity<>(apiError, HttpStatus.UNAUTHORIZED);
    }

    /**
     * Handles all other unexpected exceptions.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorDto_FCA1> handleGlobalException(Exception ex) {
        ApiErrorDto_FCA1 apiError = ApiErrorDto_FCA1.builder()
                .errorCode("INTERNAL_SERVER_ERROR")
                .message("An unexpected error occurred. Please try again later.")
                .build();
        
        logger.error("An unexpected internal server error occurred", ex);
        return new ResponseEntity<>(apiError, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
```
```java
// Spring Security Configuration
// File: SecurityConfig_FCA1.java