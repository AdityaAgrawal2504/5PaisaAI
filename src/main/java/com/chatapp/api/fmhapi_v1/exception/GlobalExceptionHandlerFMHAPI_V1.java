package com.chatapp.api.fmhapi_v1.exception;

import com.chatapp.api.fmhapi_v1.model.dto.ApiErrorDtoFMHAPI_V1;
import com.chatapp.api.fmhapi_v1.model.enums.ErrorCodeFMHAPI_V1;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler to map application exceptions to structured API error responses.
 */
@ControllerAdvice("com.chatapp.api.fmhapi_v1")
public class GlobalExceptionHandlerFMHAPI_V1 {

    /**
     * Handles custom ResourceNotFoundException.
     */
    @ExceptionHandler(ResourceNotFoundExceptionFMHAPI_V1.class)
    public ResponseEntity<ApiErrorDtoFMHAPI_V1> handleResourceNotFoundException(ResourceNotFoundExceptionFMHAPI_V1 ex) {
        ApiErrorDtoFMHAPI_V1 error = ApiErrorDtoFMHAPI_V1.builder()
                .code(ex.getErrorCode().name())
                .message(ex.getMessage())
                .build();
        return new ResponseEntity<>(error, ex.getHttpStatus());
    }

    /**
     * Handles custom AccessDeniedException.
     */
    @ExceptionHandler(AccessDeniedExceptionFMHAPI_V1.class)
    public ResponseEntity<ApiErrorDtoFMHAPI_V1> handleAccessDeniedException(AccessDeniedExceptionFMHAPI_V1 ex) {
        ApiErrorDtoFMHAPI_V1 error = ApiErrorDtoFMHAPI_V1.builder()
                .code(ex.getErrorCode().name())
                .message(ex.getMessage())
                .build();
        return new ResponseEntity<>(error, ex.getHttpStatus());
    }
    
    /**
     * Handles custom InvalidCursorException.
     */
    @ExceptionHandler(InvalidCursorExceptionFMHAPI_V1.class)
    public ResponseEntity<ApiErrorDtoFMHAPI_V1> handleInvalidCursorException(InvalidCursorExceptionFMHAPI_V1 ex) {
        ApiErrorDtoFMHAPI_V1 error = ApiErrorDtoFMHAPI_V1.builder()
                .code(ex.getErrorCode().name())
                .message(ex.getMessage())
                .build();
        return new ResponseEntity<>(error, ex.getHttpStatus());
    }

    /**
     * Handles validation exceptions from @Validated annotations.
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiErrorDtoFMHAPI_V1> handleConstraintViolationException(ConstraintViolationException ex) {
        ConstraintViolation<?> violation = ex.getConstraintViolations().iterator().next();
        String field = violation.getPropertyPath().toString();
        String value = (violation.getInvalidValue() == null) ? "null" : violation.getInvalidValue().toString();

        Map<String, Object> details = new HashMap<>();
        details.put("field", field.substring(field.lastIndexOf('.') + 1));
        details.put("value", value);
        details.put("reason", violation.getMessage());

        ErrorCodeFMHAPI_V1 errorCode = field.contains("conversationId") ?
            ErrorCodeFMHAPI_V1.INVALID_PATH_PARAMETER : ErrorCodeFMHAPI_V1.INVALID_QUERY_PARAMETER;

        ApiErrorDtoFMHAPI_V1 error = ApiErrorDtoFMHAPI_V1.builder()
                .code(errorCode.name())
                .message(violation.getMessage())
                .details(details)
                .build();
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
    
    /**
     * Handles generic exceptions as a fallback.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorDtoFMHAPI_V1> handleGenericException(Exception ex) {
        // In production, log the full exception ex
        ApiErrorDtoFMHAPI_V1 error = ApiErrorDtoFMHAPI_V1.builder()
                .code(ErrorCodeFMHAPI_V1.INTERNAL_SERVER_ERROR.name())
                .message("An unexpected error occurred. Please try again later.")
                .build();
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
