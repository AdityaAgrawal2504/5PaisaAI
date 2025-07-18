package com.example.userregistration.exception;

import com.example.userregistration.dto.ErrorResponse;
import com.example.userregistration.enums.ErrorCode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LogManager.getLogger(GlobalExceptionHandler.class);

    /**
     * Handles validation errors from @Valid annotation.
     * @param ex The exception thrown on validation failure.
     * @return A ResponseEntity containing a structured error response.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<ErrorResponse.ErrorDetail> details = ex.getBindingResult().getFieldErrors().stream()
            .map(error -> new ErrorResponse.ErrorDetail(error.getField(), error.getDefaultMessage()))
            .collect(Collectors.toList());

        ErrorResponse errorResponse = ErrorResponse.builder()
            .errorCode(ErrorCode.VALIDATION_ERROR)
            .message("Input validation failed.")
            .details(details)
            .build();

        logger.warn("Validation failed: {}", errorResponse);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles conflicts when a user with the given phone number already exists.
     * @param ex The custom exception for user conflicts.
     * @return A ResponseEntity with a 409 Conflict status.
     */
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyExistsException(UserAlreadyExistsException ex) {
        ErrorResponse errorResponse = ErrorResponse.builder()
            .errorCode(ErrorCode.USER_ALREADY_EXISTS)
            .message(ex.getMessage())
            .build();

        logger.warn("Registration conflict for phone number {}: {}", ex.getPhoneNumber(), ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    /**
     * Handles exceptions related to database access.
     * @param ex The DataAccessException that occurred.
     * @return A ResponseEntity with a 500 Internal Server Error status.
     */
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ErrorResponse> handleDatabaseException(DataAccessException ex) {
        logger.error("Database access error occurred.", ex);
        ErrorResponse errorResponse = ErrorResponse.builder()
            .errorCode(ErrorCode.DATABASE_ERROR)
            .message("A database error occurred. Please try again later.")
            .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Catches all other unhandled exceptions as a last resort.
     * @param ex The generic exception.
     * @return A ResponseEntity with a 500 Internal Server Error status.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        logger.error("An unexpected error occurred.", ex);
        ErrorResponse errorResponse = ErrorResponse.builder()
            .errorCode(ErrorCode.INTERNAL_SERVER_ERROR)
            .message("An unexpected error occurred while processing your request.")
            .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
```
// src/main/java/com/example/userregistration/repository/UserRepository.java
```java