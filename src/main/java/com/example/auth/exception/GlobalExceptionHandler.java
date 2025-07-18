package com.example.auth.exception;

import com.example.auth.dto.ApiError;
import com.example.auth.enums.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.stream.Collectors;

/**
 * Global exception handler to catch and format exceptions into standard ApiError responses.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handles custom ApiException and returns a structured error response.
     * @param ex The caught ApiException.
     * @return A ResponseEntity containing the ApiError.
     */
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiError> handleApiException(ApiException ex) {
        log.warn("API Exception: Status={}, Code={}, Message='{}'", ex.getStatus(), ex.getApiError().getErrorCode(), ex.getMessage());
        return new ResponseEntity<>(ex.getApiError(), ex.getStatus());
    }

    /**
     * Handles validation exceptions from @Valid annotations.
     * @param ex The caught MethodArgumentNotValidException.
     * @return A ResponseEntity containing a formatted validation error.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationException(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
            .map(error -> String.format("Field '%s': %s", error.getField(), error.getDefaultMessage()))
            .collect(Collectors.joining("; "));
        
        ApiError apiError = new ApiError(ErrorCode.VALIDATION_ERROR, errorMessage);
        log.warn("Validation Exception: {}", errorMessage);
        return new ResponseEntity<>(apiError, org.springframework.http.HttpStatus.BAD_REQUEST);
    }

    /**
     * Catches all other unhandled exceptions to prevent stack traces from being exposed.
     * @param ex The caught generic Exception.
     * @return A ResponseEntity with a generic internal server error message.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGenericException(Exception ex) {
        log.error("Unhandled Exception: ", ex);
        ApiError apiError = new ApiError(ErrorCode.INTERNAL_SERVER_ERROR, "An unexpected internal server error occurred.");
        return new ResponseEntity<>(apiError, org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR);
    }
}


// =================================================================
// LOGGING UTILITIES
// =================================================================