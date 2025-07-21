package com.example.logininitiation.service;

import com.example.logininitiation.exception.OtpServiceExceptionLIA_9371;
import com.example.logininitiation.util.StructuredLoggerLIA_9371;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OtpServiceImplLIA_9371Test {

    @Mock
    private StructuredLoggerLIA_9371 logger;

    @InjectMocks
    private OtpServiceImplLIA_9371 otpService;

    @Test
    void generateAndSendOtp_shouldSucceed_whenFailureRateIsZero() {
        ReflectionTestUtils.setField(otpService, "failureRate", 0.0);
        String phoneNumber = "1234567890";
        String correlationId = "test-corr-id";

        assertDoesNotThrow(() -> otpService.generateAndSendOtp(phoneNumber, correlationId));

        verify(logger, times(1)).logOperationStart(anyString(), anyLong());
        verify(logger, times(1)).logInfo(anyString(), any(String.class));
        verify(logger, times(1)).logOperationEnd(anyString(), anyLong());
    }

    @Test
    void generateAndSendOtp_shouldThrowOtpServiceException_whenFailureRateIsOne() {
        ReflectionTestUtils.setField(otpService, "failureRate", 1.0);
        String phoneNumber = "1234567890";
        String correlationId = "test-corr-id";

        assertThrows(OtpServiceExceptionLIA_9371.class, () -> otpService.generateAndSendOtp(phoneNumber, correlationId));

        verify(logger, times(1)).logOperationStart(anyString(), anyLong());
        verify(logger, times(1)).logOperationEnd(anyString(), anyLong());
        verify(logger, times(0)).logInfo(anyString(), any(String.class));
    }
}
```
src/test/java/com/example/logininitiation/service/UserServiceImplLIA_9371Test.java
```java