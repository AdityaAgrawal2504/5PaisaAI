package com.example.loginverification.service;

import com.example.loginverification.dto.LoginVerificationRequest_LVA1;
import com.example.loginverification.dto.LoginVerificationResponse_LVA1;
import com.example.loginverification.exception.InvalidOtpException_LVA1;
import com.example.loginverification.exception.OtpExpiredException_LVA1;
import com.example.loginverification.exception.ResourceNotFoundException_LVA1;
import com.example.loginverification.logging.LoggingService_LVA1;
import com.example.loginverification.util.JwtTokenUtil_LVA1;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class LoginVerificationService_LVA1 {

    private final JwtTokenUtil_LVA1 jwtTokenUtil;
    private final OtpStoreService_LVA1 otpStoreService;
    private final RateLimiterService_LVA1 rateLimiterService;
    private final LoggingService_LVA1 loggingService;
    private final long tokenExpirationSeconds;
    private final String tokenType;

    public LoginVerificationService_LVA1(JwtTokenUtil_LVA1 jwtTokenUtil,
                                       OtpStoreService_LVA1 otpStoreService,
                                       RateLimiterService_LVA1 rateLimiterService,
                                       LoggingService_LVA1 loggingService,
                                       @Value("${jwt.expiration.seconds}") long tokenExpirationSeconds,
                                       @Value("${jwt.token.type}") String tokenType) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.otpStoreService = otpStoreService;
        this.rateLimiterService = rateLimiterService;
        this.loggingService = loggingService;
        this.tokenExpirationSeconds = tokenExpirationSeconds;
        this.tokenType = tokenType;
    }

    /**
     * Verifies the user's phone number and OTP, returning a JWT on success.
     * @param request The login verification request containing phone number and OTP.
     * @return A response containing the auth token, type, and expiration.
     */
    public LoginVerificationResponse_LVA1 verifyLogin(LoginVerificationRequest_LVA1 request) {
        long startTime = loggingService.logFunctionStart("verifyLogin");
        
        try {
            String phoneNumber = request.getPhoneNumber();
            String otp = request.getOtp();

            // Throws RateLimitException if blocked, which is handled by the GlobalExceptionHandler
            rateLimiterService.recordFailedAttempt(phoneNumber);

            OtpStoreService_LVA1.OtpDetails otpDetails = otpStoreService.getOtpDetails(phoneNumber);

            if (otpDetails == null) {
                loggingService.logWarn("Verification failed for phone number {}: user not found or no pending OTP.", phoneNumber);
                throw new ResourceNotFoundException_LVA1();
            }

            if (otpStoreService.isOtpExpired(otpDetails)) {
                loggingService.logWarn("Verification failed for phone number {}: OTP expired.", phoneNumber);
                otpStoreService.clearOtp(phoneNumber); // Clean up expired OTP
                throw new OtpExpiredException_LVA1();
            }

            if (!otpDetails.otp().equals(otp)) {
                loggingService.logWarn("Verification failed for phone number {}: invalid OTP provided.", phoneNumber);
                // Note: The failed attempt was already recorded at the start of the method.
                throw new InvalidOtpException_LVA1();
            }

            // Success
            loggingService.logInfo("OTP verification successful for phone number: {}", phoneNumber);
            rateLimiterService.resetAttempts(phoneNumber); // Clear rate limit attempts on success
            otpStoreService.clearOtp(phoneNumber); // Invalidate OTP after use

            String token = jwtTokenUtil.generateToken(phoneNumber);
            
            return new LoginVerificationResponse_LVA1(token, tokenType, tokenExpirationSeconds);

        } finally {
            loggingService.logFunctionEnd("verifyLogin", startTime);
        }
    }
}
```
src/main/java/com/example/loginverification/controller/LoginVerificationController_LVA1.java
```java