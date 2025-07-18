package com.example.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Data Transfer Object for the login request payload.
 */
@Data
public class LoginRequest {

    /**
     * User's registered phone number in E.164 format.
     */
    @NotBlank(message = "Phone number is required.")
    @Pattern(regexp = "^\\+[1-9]\\d{1,14}$", message = "Phone number must be in E.164 format.")
    @Size(min = 10, max = 15, message = "Phone number must be between 10 and 15 characters.")
    private String phoneNumber;

    /**
     * User's password.
     */
    @NotBlank(message = "Password is required.")
    @Size(min = 8, message = "Password must be at least 8 characters long.")
    private String password;
}
