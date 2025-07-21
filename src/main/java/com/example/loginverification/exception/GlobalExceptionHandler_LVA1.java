package com.example.loginverification.exception;

import com.example.loginverification.dto.ApiError_LVA1;
import com.example.loginverification.model.ErrorCode_LVA1;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler_LVA1 extends ResponseEntityExceptionHandler {

    private static final Logger log = LogManager.getLogger(GlobalExceptionHandler_LVA1.class);

    /**
     * Handles custom application-specific exceptions.
     */
    @ExceptionHandler(ApiException_LVA1.class)
    public ResponseEntity<ApiError_LVA1> handleApiException(ApiException_LVA1 ex, WebRequest request) {
        ApiError_LVA1 apiError = new ApiError_LVA1(ex.getErrorCode());
        log.warn("API Error: {} - Path: {}", apiError.getErrorMessage(), request.getDescription(false), ex);
        return new ResponseEntity<>(apiError, ex.getHttpStatus());
    }

    /**
     * Handles bean validation exceptions.
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        
        String errors = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .collect(Collectors.joining(", "));

        String errorMessage = ErrorCode_LVA1.VALIDATION_ERROR.getMessage() + " Details: " + errors;
        ApiError_LVA1 apiError = new ApiError_LVA1(ErrorCode_LVA1.VALIDATION_ERROR.getCode(), errorMessage);

        log.warn("Validation Error: {} - Path: {}", errorMessage, request.getDescription(false));
        return new ResponseEntity<>(apiError, ErrorCode_LVA1.VALIDATION_ERROR.getStatus());
    }

    /**
     * Handles all other un-caught exceptions as a fallback.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError_LVA1> handleGlobalException(Exception ex, WebRequest request) {
        ErrorCode_LVA1 errorCode = ErrorCode_LVA1.INTERNAL_SERVER_ERROR;
        ApiError_LVA1 apiError = new ApiError_LVA1(errorCode);
        log.error("Unhandled Internal Server Error at path: {}", request.getDescription(false), ex);
        return new ResponseEntity<>(apiError, errorCode.getStatus());
    }
}
```
src/main/java/com/example/loginverification/logging/LoggingService_LVA1.java
```java