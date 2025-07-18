package com.example.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for a successful login response.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    /**
     * A confirmation message indicating OTP has been sent.
     */
    private String message;

    /**
     * Token to be used in the subsequent OTP verification step.
     */
    private String otpSessionToken;
}
