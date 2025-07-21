package com.example.loginverification.service;

import com.example.loginverification.dto.LoginVerificationRequest_LVA1;
import com.example.loginverification.dto.LoginVerificationResponse_LVA1;
import com.example.loginverification.exception.InvalidOtpException_LVA1;
import com.example.loginverification.exception.OtpExpiredException_LVA1;
import com.example.loginverification.exception.RateLimitException_LVA1;
import com.example.loginverification.exception.ResourceNotFoundException_LVA1;
import com.example.loginverification.logging.LoggingService_LVA1;
import com.example.loginverification.util.JwtTokenUtil_LVA1;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginVerificationServiceTest_LVA1 {

    @Mock
    private JwtTokenUtil_LVA1 jwtTokenUtil;
    @Mock
    private OtpStoreService_LVA1 otpStoreService;
    @Mock
    private RateLimiterService_LVA1 rateLimiterService;
    @Mock
    private LoggingService_LVA1 loggingService;

    @InjectMocks
    private LoginVerificationService_LVA1 loginVerificationService;

    private LoginVerificationRequest_LVA1 request;
    private final String phoneNumber = "+14155552671";
    private final String correctOtp = "123456";
    private final String incorrectOtp = "654321";

    @BeforeEach
    void setUp() {
        request = new LoginVerificationRequest_LVA1(phoneNumber, correctOtp);
        // Inject values manually as @Value won't work in this test setup
        loginVerificationService = new LoginVerificationService_LVA1(jwtTokenUtil, otpStoreService, rateLimiterService, loggingService, 3600, "Bearer");
        when(loggingService.logFunctionStart(anyString())).thenReturn(System.nanoTime());
    }

    @Test
    void verifyLogin_Success() {
        OtpStoreService_LVA1.OtpDetails validOtpDetails = new OtpStoreService_LVA1.OtpDetails(correctOtp, LocalDateTime.now());
        when(otpStoreService.getOtpDetails(phoneNumber)).thenReturn(validOtpDetails);
        when(otpStoreService.isOtpExpired(validOtpDetails)).thenReturn(false);
        when(jwtTokenUtil.generateToken(phoneNumber)).thenReturn("test.jwt.token");

        LoginVerificationResponse_LVA1 response = loginVerificationService.verifyLogin(request);

        assertNotNull(response);
        assertEquals("test.jwt.token", response.getAuthToken());
        assertEquals("Bearer", response.getTokenType());
        assertEquals(3600, response.getExpiresIn());

        verify(rateLimiterService, times(1)).recordFailedAttempt(phoneNumber);
        verify(rateLimiterService, times(1)).resetAttempts(phoneNumber);
        verify(otpStoreService, times(1)).clearOtp(phoneNumber);
    }

    @Test
    void verifyLogin_ThrowsResourceNotFoundException_WhenOtpDetailsNull() {
        when(otpStoreService.getOtpDetails(phoneNumber)).thenReturn(null);

        assertThrows(ResourceNotFoundException_LVA1.class, () -> loginVerificationService.verifyLogin(request));
        
        verify(rateLimiterService, times(1)).recordFailedAttempt(phoneNumber);
        verify(rateLimiterService, never()).resetAttempts(any());
        verify(otpStoreService, never()).clearOtp(any());
    }

    @Test
    void verifyLogin_ThrowsOtpExpiredException_WhenOtpIsExpired() {
        OtpStoreService_LVA1.OtpDetails expiredOtpDetails = new OtpStoreService_LVA1.OtpDetails(correctOtp, LocalDateTime.now().minusMinutes(10));
        when(otpStoreService.getOtpDetails(phoneNumber)).thenReturn(expiredOtpDetails);
        when(otpStoreService.isOtpExpired(expiredOtpDetails)).thenReturn(true);

        assertThrows(OtpExpiredException_LVA1.class, () -> loginVerificationService.verifyLogin(request));

        verify(otpStoreService, times(1)).clearOtp(phoneNumber);
        verify(rateLimiterService, times(1)).recordFailedAttempt(phoneNumber);
    }

    @Test
    void verifyLogin_ThrowsInvalidOtpException_WhenOtpIsIncorrect() {
        request.setOtp(incorrectOtp);
        OtpStoreService_LVA1.OtpDetails validOtpDetails = new OtpStoreService_LVA1.OtpDetails(correctOtp, LocalDateTime.now());
        when(otpStoreService.getOtpDetails(phoneNumber)).thenReturn(validOtpDetails);
        when(otpStoreService.isOtpExpired(validOtpDetails)).thenReturn(false);

        assertThrows(InvalidOtpException_LVA1.class, () -> loginVerificationService.verifyLogin(request));

        verify(rateLimiterService, times(1)).recordFailedAttempt(phoneNumber);
        verify(rateLimiterService, never()).resetAttempts(any());
        verify(otpStoreService, never()).clearOtp(any());
    }
    
    @Test
    void verifyLogin_ThrowsRateLimitException_WhenTooManyAttempts() {
        doThrow(new RateLimitException_LVA1()).when(rateLimiterService).recordFailedAttempt(phoneNumber);

        assertThrows(RateLimitException_LVA1.class, () -> loginVerificationService.verifyLogin(request));

        verify(otpStoreService, never()).getOtpDetails(any());
        verify(jwtTokenUtil, never()).generateToken(any());
    }
}
```
src/test/java/com/example/loginverification/controller/LoginVerificationControllerTest_LVA1.java
```java