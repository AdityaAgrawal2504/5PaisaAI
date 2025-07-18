package com.example.auth.exception;

import com.example.auth.dto.ApiError;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Custom runtime exception used to signal API-specific errors.
 */
@Getter
public class ApiException extends RuntimeException {

    private final HttpStatus status;
    private final ApiError apiError;

    /**
     * Constructs a new ApiException.
     * @param status The HTTP status to be returned.
     * @param apiError The structured error DTO.
     */
    public ApiException(HttpStatus status, ApiError apiError) {
        super(apiError.getErrorMessage());
        this.status = status;
        this.apiError = apiError;
    }
}
