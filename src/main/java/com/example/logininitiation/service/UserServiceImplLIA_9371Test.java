package com.example.logininitiation.service;

import com.example.logininitiation.exception.AuthenticationFailedExceptionLIA_9371;
import com.example.logininitiation.model.UserLIA_9371;
import com.example.logininitiation.repository.UserRepositoryLIA_9371;
import com.example.logininitiation.util.StructuredLoggerLIA_9371;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplLIA_9371Test {

    @Mock
    private UserRepositoryLIA_9371 userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private StructuredLoggerLIA_9371 logger;

    @InjectMocks
    private UserServiceImplLIA_9371 userService;

    private UserLIA_9371 testUser;
    private final String phoneNumber = "1234567890";
    private final String rawPassword = "rawPassword123";
    private final String hashedPassword = "hashedPassword123";

    @BeforeEach
    void setUp() {
        testUser = new UserLIA_9371();
        testUser.setId(1L);
        testUser.setPhoneNumber(phoneNumber);
        testUser.setPasswordHash(hashedPassword);
        testUser.setEnabled(true);
        testUser.setAccountLocked(false);
    }

    @Test
    void findAndValidateCredentials_shouldReturnUser_whenCredentialsAreValid() {
        when(userRepository.findByPhoneNumber(phoneNumber)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(rawPassword, hashedPassword)).thenReturn(true);

        UserLIA_9371 result = userService.findAndValidateCredentials(phoneNumber, rawPassword);

        assertNotNull(result);
        assertEquals(phoneNumber, result.getPhoneNumber());
    }

    @Test
    void findAndValidateCredentials_shouldThrowException_whenUserNotFound() {
        when(userRepository.findByPhoneNumber(anyString())).thenReturn(Optional.empty());

        assertThrows(AuthenticationFailedExceptionLIA_9371.class,
                () -> userService.findAndValidateCredentials(phoneNumber, rawPassword));
    }

    @Test
    void findAndValidateCredentials_shouldThrowException_whenPasswordIsIncorrect() {
        when(userRepository.findByPhoneNumber(phoneNumber)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(rawPassword, hashedPassword)).thenReturn(false);

        assertThrows(AuthenticationFailedExceptionLIA_9371.class,
                () -> userService.findAndValidateCredentials(phoneNumber, rawPassword));
    }

    @Test
    void findAndValidateCredentials_shouldThrowException_whenAccountIsLocked() {
        testUser.setAccountLocked(true);
        when(userRepository.findByPhoneNumber(phoneNumber)).thenReturn(Optional.of(testUser));

        assertThrows(AuthenticationFailedExceptionLIA_9371.class,
                () -> userService.findAndValidateCredentials(phoneNumber, rawPassword));
    }

    @Test
    void findAndValidateCredentials_shouldThrowException_whenAccountIsDisabled() {
        testUser.setEnabled(false);
        when(userRepository.findByPhoneNumber(phoneNumber)).thenReturn(Optional.of(testUser));

        assertThrows(AuthenticationFailedExceptionLIA_9371.class,
                () -> userService.findAndValidateCredentials(phoneNumber, rawPassword));
    }
}
```
src/test/java/com/example/logininitiation/service/AuthenticationServiceImplLIA_9371Test.java
```java