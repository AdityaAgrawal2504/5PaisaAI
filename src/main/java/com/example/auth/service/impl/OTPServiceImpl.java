package com.example.auth.service.impl;

import com.example.auth.dto.ApiError;
import com.example.auth.enums.ErrorCode;
import com.example.auth.exception.ApiException;
import com.example.auth.service.IOTPService;
import com.example.auth.util.logging.Loggable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.text.DecimalFormat;

/**
 * Mock implementation of IOTPService for generating and "sending" One-Time Passwords.
 */
@Service
public class OTPServiceImpl implements IOTPService {

    private static final Logger log = LoggerFactory.getLogger(OTPServiceImpl.class);
    private final SecureRandom secureRandom = new SecureRandom();

    /**
     * Generates a 6-digit OTP and logs it to the console instead of sending an SMS.
     * Simulates a potential failure for demonstration purposes.
     * @param phoneNumber The destination phone number.
     * @param userId The ID of the user.
     * @throws ApiException if the simulated OTP sending fails.
     */
    @Override
    @Loggable
    public void generateAndSendOtp(String phoneNumber, String userId) {
        String otp = new DecimalFormat("000000").format(secureRandom.nextInt(999999));

        // Simulate a potential failure in the OTP sending service
        if (phoneNumber.endsWith("0")) { // Artificial failure condition
             log.error("Simulated OTP send failure for user {} to phone number {}", userId, phoneNumber);
             throw new ApiException(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR, 
                new ApiError(ErrorCode.OTP_SEND_FAILURE, "We were unable to send an OTP to your mobile number. Please try again."));
        }

        // In a real application, this is where you would integrate with Twilio, Vonage, etc.
        // For example: smsGateway.send(phoneNumber, "Your verification code is: " + otp);
        log.info("---- MOCK OTP SERVICE ----");
        log.info("Sending OTP {} to user {} at phone number {}", otp, userId, phoneNumber);
        log.info("--------------------------");
    }
}
