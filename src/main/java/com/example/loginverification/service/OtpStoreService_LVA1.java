package com.example.loginverification.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A mock service to store and manage OTP data in-memory.
 * In a production system, this would be replaced by a distributed cache like Redis.
 */
@Service
public class OtpStoreService_LVA1 {

    private static final Map<String, OtpDetails> otpStore = new ConcurrentHashMap<>();
    private final long otpExpiryMinutes;

    public OtpStoreService_LVA1(@Value("${mock.otp.expiry.minutes}") long otpExpiryMinutes,
                               @Value("${mock.user.phone.number}") String mockPhoneNumber,
                               @Value("${mock.otp.value}") String mockOtp) {
        this.otpExpiryMinutes = otpExpiryMinutes;
        // Pre-populate with a valid OTP for demonstration and testing
        otpStore.put(mockPhoneNumber, new OtpDetails(mockOtp, LocalDateTime.now()));
    }

    /**
     * Retrieves OTP details for a given phone number.
     * @param phoneNumber The phone number to look up.
     * @return OtpDetails if found, null otherwise.
     */
    public OtpDetails getOtpDetails(String phoneNumber) {
        return otpStore.get(phoneNumber);
    }

    /**
     * Checks if the stored OTP has expired.
     * @param details The OTP details to check.
     * @return true if expired, false otherwise.
     */
    public boolean isOtpExpired(OtpDetails details) {
        return details.getCreationTime().plusMinutes(otpExpiryMinutes).isBefore(LocalDateTime.now());
    }
    
    /**
     * Removes the OTP from the store, typically after successful verification.
     * @param phoneNumber The phone number whose OTP should be cleared.
     */
    public void clearOtp(String phoneNumber) {
        otpStore.remove(phoneNumber);
    }

    /**
     * Inner class to hold OTP data.
     */
    public static record OtpDetails(String otp, LocalDateTime creationTime) {
        public String getOtp() {
            return otp;
        }

        public LocalDateTime getCreationTime() {
            return creationTime;
        }
    }
}
```
src/main/java/com/example/loginverification/service/RateLimiterService_LVA1.java
```java