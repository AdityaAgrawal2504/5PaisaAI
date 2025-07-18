package com.example.auth.service;

/**
 * Manages the generation and delivery of One-Time Passwords.
 */
public interface IOTPService {
    /**
     * Generates a new OTP and sends it to the user's phone.
     * In a real application, this would integrate with an SMS gateway.
     * @param phoneNumber The destination phone number for the OTP.
     * @param userId The ID of the user requesting the OTP.
     */
    void generateAndSendOtp(String phoneNumber, String userId);
}
