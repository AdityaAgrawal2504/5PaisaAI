package com.omqs.exception;

import com.omqs.constants.ErrorCode_OMQS1;
import com.omqs.dto.ErrorResponseDto_OMQS1;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler to translate exceptions into standardized error responses.
 */
@ControllerAdvice
@Log4j2
public class GlobalExceptionHandler_OMQS1 {

    /**
     * Handles JSR-303 validation exceptions for request bodies.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto_OMQS1> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
            errors.put(error.getField(), error.getDefaultMessage()));

        ErrorResponseDto_OMQS1 errorResponse = new ErrorResponseDto_OMQS1(
            ErrorCode_OMQS1.VALIDATION_ERROR,
            "One or more fields failed validation.",
            errors
        );
        log.warn("Validation error: {}", errors);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles validation exceptions for path variables and request parameters.
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponseDto_OMQS1> handleConstraintViolation(ConstraintViolationException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getConstraintViolations().forEach(cv -> {
            String field = cv.getPropertyPath().toString();
            // Simplify field name (e.g., "dequeueMessages.limit" -> "limit")
            String simpleField = field.substring(field.lastIndexOf('.') + 1);
            errors.put(simpleField, cv.getMessage());
        });

        ErrorResponseDto_OMQS1 errorResponse = new ErrorResponseDto_OMQS1(
            ErrorCode_OMQS1.VALIDATION_ERROR,
            "Validation failed for request parameters.",
            errors
        );
        log.warn("Constraint violation: {}", errors);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    
    /**
     * Handles custom business logic validation exceptions.
     */
    @ExceptionHandler(ValidationException_OMQS1.class)
    public ResponseEntity<ErrorResponseDto_OMQS1> handleCustomValidationException(ValidationException_OMQS1 ex) {
        ErrorResponseDto_OMQS1 errorResponse = new ErrorResponseDto_OMQS1(
            ErrorCode_OMQS1.VALIDATION_ERROR,
            ex.getMessage()
        );
        log.warn("Business validation failed: {}", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles exceptions for type mismatches in request paths/params (e.g., invalid UUID format).
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponseDto_OMQS1> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String errorMessage = String.format("Parameter '%s' has an invalid value: '%s'. Expected type is '%s'.",
                ex.getName(), ex.getValue(), ex.getRequiredType().getSimpleName());

        ErrorResponseDto_OMQS1 errorResponse = new ErrorResponseDto_OMQS1(
                ErrorCode_OMQS1.VALIDATION_ERROR,
                errorMessage
        );
        log.warn("Method argument type mismatch: {}", errorMessage);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles custom resource not found exceptions.
     */
    @ExceptionHandler(ResourceNotFoundException_OMQS1.class)
    public ResponseEntity<ErrorResponseDto_OMQS1> handleResourceNotFound(ResourceNotFoundException_OMQS1 ex) {
        ErrorResponseDto_OMQS1 errorResponse = new ErrorResponseDto_OMQS1(
            ErrorCode_OMQS1.RESOURCE_NOT_FOUND,
            ex.getMessage()
        );
        log.warn("Resource not found: {}", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    /**
     * Handles exceptions related to database/persistence operations.
     */
    @ExceptionHandler({PersistenceOperationException_OMQS1.class, DataAccessException.class})
    public ResponseEntity<ErrorResponseDto_OMQS1> handlePersistenceException(Exception ex) {
        log.error("Persistence failure", ex);
        ErrorResponseDto_OMQS1 errorResponse = new ErrorResponseDto_OMQS1(
            ErrorCode_OMQS1.PERSISTENCE_FAILURE,
            "The service failed to complete a database operation."
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Catches all other unhandled exceptions.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto_OMQS1> handleAllExceptions(Exception ex) {
        log.error("An unexpected error occurred", ex);
        ErrorResponseDto_OMQS1 errorResponse = new ErrorResponseDto_OMQS1(
            ErrorCode_OMQS1.SERVICE_UNAVAILABLE,
            "An unexpected internal error occurred."
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
```
```java
// src/main/java/com/omqs/aspect/LoggingAspect_OMQS1.java