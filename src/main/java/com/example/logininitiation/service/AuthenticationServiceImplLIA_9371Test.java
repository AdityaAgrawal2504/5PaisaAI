package com.example.logininitiation.service;

import com.example.logininitiation.dto.LoginInitiationRequestLIA_9371;
import com.example.logininitiation.dto.LoginInitiationResponseLIA_9371;
import com.example.logininitiation.exception.AuthenticationFailedExceptionLIA_9371;
import com.example.logininitiation.exception.OtpServiceExceptionLIA_9371;
import com.example.logininitiation.model.UserLIA_9371;
import com.example.logininitiation.util.StructuredLoggerLIA_9371;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceImplLIA_9371Test {

    @Mock
    private IUserServiceLIA_9371 userService;

    @Mock
    private IOTPServiceLIA_9371 otpService;
    
    @Mock
    private StructuredLoggerLIA_9371 logger;

    @InjectMocks
    private AuthenticationServiceImplLIA_9371 authenticationService;

    private LoginInitiationRequestLIA_9371 request;
    private UserLIA_9371 validUser;

    @BeforeEach
    void setUp() {
        request = new LoginInitiationRequestLIA_9371("1234567890", "ValidPass!123");
        validUser = new UserLIA_9371();
        validUser.setPhoneNumber("1234567890");
    }

    @Test
    void initiateLogin_shouldSucceed_whenCredentialsAreValid() {
        when(userService.findAndValidateCredentials(anyString(), anyString())).thenReturn(validUser);
        doNothing().when(otpService).generateAndSendOtp(anyString(), anyString());

        LoginInitiationResponseLIA_9371 response = authenticationService.initiateLogin(request);

        assertNotNull(response);
        assertEquals("An OTP has been sent to your registered phone number.", response.getMessage());
        assertNotNull(response.getCorrelationId());
        verify(userService, times(1)).findAndValidateCredentials(request.getPhoneNumber(), request.getPassword());
        verify(otpService, times(1)).generateAndSendOtp(eq(validUser.getPhoneNumber()), anyString());
    }

    @Test
    void initiateLogin_shouldThrowAuthenticationFailedException_whenCredentialsAreInvalid() {
        when(userService.findAndValidateCredentials(anyString(), anyString()))
                .thenThrow(new AuthenticationFailedExceptionLIA_9371("Invalid credentials"));

        assertThrows(AuthenticationFailedExceptionLIA_9371.class, () -> authenticationService.initiateLogin(request));

        verify(otpService, never()).generateAndSendOtp(anyString(), anyString());
    }

    @Test
    void initiateLogin_shouldThrowOtpServiceException_whenOtpServiceFails() {
        when(userService.findAndValidateCredentials(anyString(), anyString())).thenReturn(validUser);
        doThrow(new OtpServiceExceptionLIA_9371("OTP provider down"))
                .when(otpService).generateAndSendOtp(anyString(), anyString());

        assertThrows(OtpServiceExceptionLIA_9371.class, () -> authenticationService.initiateLogin(request));
    }
}
```
src/test/java/com/example/logininitiation/api/LoginInitiationControllerLIA_9371Test.java
```java