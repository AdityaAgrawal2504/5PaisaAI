package com.example.auth.dto;

import com.example.auth.enums.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Standard structure for API error responses.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiError {

    /**
     * A machine-readable error code.
     */
    private ErrorCode errorCode;

    /**
     * A human-readable description of the error.
     */
    private String errorMessage;
}


// =================================================================
// ENUMS
// =================================================================
