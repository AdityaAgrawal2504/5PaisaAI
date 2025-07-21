package com.example.logininitiation.exception;

import com.example.logininitiation.dto.ApiErrorLIA_9371;
import com.example.logininitiation.enums.ErrorCodeLIA_9371;
import com.example.logininitiation.util.StructuredLoggerLIA_9371;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * Global exception handler to catch exceptions and format them into a standard ApiError response.
 */
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandlerLIA_9371 {

    private final StructuredLoggerLIA_9371 logger;

    /**
     * Handles validation errors from @Valid annotation on request bodies.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorLIA_9371> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        ApiErrorLIA_9371 apiError = new ApiErrorLIA_9371(ErrorCodeLIA_9371.INVALID_INPUT, errorMessage);
        logger.logError("Validation failed", ex, "errorCode", apiError.getErrorCode().name());
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles custom authentication failure exceptions.
     */
    @ExceptionHandler(AuthenticationFailedExceptionLIA_9371.class)
    public ResponseEntity<ApiErrorLIA_9371> handleAuthenticationFailedException(AuthenticationFailedExceptionLIA_9371 ex) {
        ApiErrorLIA_9371 apiError = new ApiErrorLIA_9371(ErrorCodeLIA_9371.AUTHENTICATION_FAILED, ex.getMessage());
        logger.logWarning("Authentication attempt failed", "reason", ex.getMessage());
        return new ResponseEntity<>(apiError, HttpStatus.UNAUTHORIZED);
    }

    /**
     * Handles custom OTP service failure exceptions.
     */
    @ExceptionHandler(OtpServiceExceptionLIA_9371.class)
    public ResponseEntity<ApiErrorLIA_9371> handleOtpServiceException(OtpServiceExceptionLIA_9371 ex) {
        ApiErrorLIA_9371 apiError = new ApiErrorLIA_9371(ErrorCodeLIA_9371.OTP_SERVICE_FAILURE, ex.getMessage());
        logger.logError("OTP Service interaction failed", ex, "errorCode", apiError.getErrorCode().name());
        return new ResponseEntity<>(apiError, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Handles all other unexpected exceptions.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorLIA_9371> handleAllOtherExceptions(Exception ex) {
        ApiErrorLIA_9371 apiError = new ApiErrorLIA_9371(ErrorCodeLIA_9371.INTERNAL_SERVER_ERROR, "An unexpected internal error occurred.");
        logger.logError("An unhandled exception occurred", ex, "errorCode", apiError.getErrorCode().name());
        return new ResponseEntity<>(apiError, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
```
src/main/java/com/example/logininitiation/model/UserLIA_9371.java
```java