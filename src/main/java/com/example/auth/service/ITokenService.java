package com.example.auth.service;

/**
 * Manages the creation and validation of JWTs or other tokens.
 */
public interface ITokenService {
    /**
     * Creates a short-lived token to track the OTP verification process.
     * @param userId The ID of the user for whom the token is generated.
     * @return A JWT string representing the OTP session.
     */
    String generateOtpSessionToken(String userId);
}


// =================================================================
// SERVICE IMPLEMENTATIONS
// =================================================================
