package com.example.auth.service;

import com.example.auth.dto.LoginRequest;
import com.example.auth.dto.LoginResponse;

/**
 * Handles the core business logic for user authentication.
 */
public interface IAuthenticationService {
    /**
     * Validates credentials, and on success, triggers OTP sending and returns a session token.
     * @param request The login request DTO containing user credentials.
     * @return A LoginResponse containing a success message and the OTP session token.
     */
    LoginResponse login(LoginRequest request);
}
