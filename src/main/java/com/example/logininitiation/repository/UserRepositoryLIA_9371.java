package com.example.logininitiation.repository;

import com.example.logininitiation.model.UserLIA_9371;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for User entities.
 */
@Repository
public interface UserRepositoryLIA_9371 extends JpaRepository<UserLIA_9371, Long> {

    /**
     * Finds a user by their phone number.
     *
     * @param phoneNumber The 10-digit phone number to search for.
     * @return An Optional containing the user if found.
     */
    Optional<UserLIA_9371> findByPhoneNumber(String phoneNumber);
}
```
src/main/java/com/example/logininitiation/util/StructuredLoggerLIA_9371.java
<ctrl62>
@Component
public class StructuredLoggerLIA_9371 {

    private static final Logger logger = LoggerFactory.getLogger(StructuredLoggerLIA_9371.class);

    /**
     * Logs the start of an operation with its duration.
     * @param operationName The name of the operation.
     * @param startTime The start time in milliseconds (from System.currentTimeMillis()).
     */
    public void logOperationStart(String operationName, long startTime) {
        MDC.put("operation", operationName);
        MDC.put("startTime", String.valueOf(startTime));
        logger.info("Operation started");
    }

    /**
     * Logs the successful completion of an operation with its duration.
     * @param operationName The name of the operation.
     * @param startTime The start time in milliseconds.
     */
    public void logOperationEnd(String operationName, long startTime) {
        long duration = System.currentTimeMillis() - startTime;
        MDC.put("operation", operationName);
        MDC.put("durationMs", String.valueOf(duration));
        logger.info("Operation finished successfully");
        MDC.remove("operation");
        MDC.remove("startTime");
        MDC.remove("durationMs");
    }

    /**
     * Logs an informational message with key-value pairs.
     * @param message The main log message.
     * @param context Key-value pairs of context data.
     */
    public void logInfo(String message, String... context) {
        withContext(context, () -> logger.info(message));
    }

    /**
     * Logs a warning message with key-value pairs.
     * @param message The main log message.
     * @param context Key-value pairs of context data.
     */
    public void logWarning(String message, String... context) {
        withContext(context, () -> logger.warn(message));
    }

    /**
     * Logs an error message with an exception and key-value pairs.
     * @param message The main log message.
     * @param throwable The exception to log.
     * @param context Key-value pairs of context data.
     */
    public void logError(String message, Throwable throwable, String... context) {
        withContext(context, () -> logger.error(message, throwable));
    }

    private void withContext(String[] context, Runnable logAction) {
        for (int i = 0; i < context.length; i += 2) {
            if (i + 1 < context.length) {
                MDC.put(context[i], context[i + 1]);
            }
        }
        logAction.run();
        for (int i = 0; i < context.length; i += 2) {
            MDC.remove(context[i]);
        }
    }
}
```
src/main/java/com/example/logininitiation/service/IOTPServiceLIA_9371.java
<ctrl62><ctrl60>package com.example.logininitiation.service;

/**
 * Responsible for generating, storing, and sending OTPs.
 */
public interface IOTPServiceLIA_9371 {

    /**
     * Generates a new OTP, associates it with the correlation ID, and sends it via an SMS provider.
     *
     * @param phoneNumber   The user's phone number to send the OTP to.
     * @param correlationId A unique ID to associate with this OTP attempt.
     */
    void generateAndSendOtp(String phoneNumber, String correlationId);
}
```
src/main/java/com/example/logininitiation/service/OtpServiceImplLIA_9371.java
<ctrl60>package com.example.logininitiation.service;

import com.example.logininitiation.exception.OtpServiceExceptionLIA_9371;
import com.example.logininitiation.util.StructuredLoggerLIA_9371;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.util.Random;

/**
 * Mock implementation of the IOTPService. Simulates OTP generation and sending.
 */
@Service
@RequiredArgsConstructor
public class OtpServiceImplLIA_9371 implements IOTPServiceLIA_9371 {

    private static final Random random = new SecureRandom();
    private static final DecimalFormat OTP_FORMAT = new DecimalFormat("000000");

    private final StructuredLoggerLIA_9371 logger;

    @Value("${otp.service.mock.failure-rate:0.0}")
    private double failureRate;

    /**
     * Generates a 6-digit OTP and logs it. Simulates sending via an SMS provider.
     * Can be configured to simulate failures for testing purposes.
     */
    @Override
    public void generateAndSendOtp(String phoneNumber, String correlationId) {
        long startTime = System.currentTimeMillis();
        logger.logOperationStart("generateAndSendOtp", startTime);

        if (Math.random() < failureRate) {
            logger.logOperationEnd("generateAndSendOtp", startTime);
            throw new OtpServiceExceptionLIA_9371("Simulated OTP service provider failure.");
        }

        String otp = OTP_FORMAT.format(random.nextInt(999999));

        // In a real application, this would integrate with an SMS gateway like Twilio or Vonage.
        // For this implementation, we just log the OTP for demonstration purposes.
        logger.logInfo(
                "Simulating sending OTP",
                "phoneNumber", phoneNumber,
                "correlationId", correlationId,
                "otp", otp // IMPORTANT: Do NOT log OTPs in a real production environment.
        );

        logger.logOperationEnd("generateAndSendOtp", startTime);
    }
}
```
src/main/java/com/example/logininitiation/service/IUserServiceLIA_9371.java
<ctrl60><ctrl62>
public interface IUserServiceLIA_9371 {

    /**
     * Finds a user by phone number and validates their password hash.
     * Throws an error if the user is not found, the account is locked, or the password does not match.
     *
     * @param phoneNumber The user's phone number.
     * @param password    The plaintext password to validate.
     * @return The validated User object.
     * @throws AuthenticationFailedExceptionLIA_9371 if validation fails.
     */
    UserLIA_9371 findAndValidateCredentials(String phoneNumber, String password);
}
```
src/main/java/com/example/logininitiation/service/UserServiceImplLIA_9371.java
<ctrl60><ctrl62> {
            logger.logWarning("Authentication failed: User not found", "phoneNumber", phoneNumber);
            throw new AuthenticationFailedExceptionLIA_9371(GENERIC_AUTH_FAILURE_MESSAGE);
        }

        UserLIA_9371 user = userOptional.get();

        if (user.isAccountLocked() || !user.isEnabled()) {
            logger.logWarning("Authentication failed: Account locked or disabled", "phoneNumber", phoneNumber);
            throw new AuthenticationFailedExceptionLIA_9371(GENERIC_AUTH_FAILURE_MESSAGE);
        }

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            // Here you might add logic to track failed attempts and lock the account.
            logger.logWarning("Authentication failed: Password mismatch", "phoneNumber", phoneNumber);
            throw new AuthenticationFailedExceptionLIA_9371(GENERIC_AUTH_FAILURE_MESSAGE);
        }

        logger.logInfo("User credentials validated successfully", "phoneNumber", phoneNumber);
        logger.logOperationEnd("findAndValidateCredentials", startTime);
        return user;
    }
}
```
src/main/java/com/example/logininitiation/service/IAuthenticationServiceLIA_9371.java
<ctrl62>
public interface IAuthenticationServiceLIA_9371 {

    /**
     * Handles the core logic of the authentication flow.
     *
     * @param request The login initiation request DTO.
     * @return A LoginInitiationResponse containing a success message and correlation ID.
     */
    LoginInitiationResponseLIA_9371 initiateLogin(LoginInitiationRequestLIA_9371 request);
}
```
src/main/java/com/example/logininitiation/service/AuthenticationServiceImplLIA_9371.java
<ctrl60><ctrl62>
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Implements the core logic for the login initiation process.
 *
 * <pre>
 * {@code
 * <!-- Mermaid.js Sequence Diagram -->
 * sequenceDiagram
 *     participant C as Client
 *     participant API as LoginController
 *     participant AuthSvc as AuthenticationService
 *     participant UserSvc as UserService
 *     participant OTPSvc as OTPService
 *
 *     C->>+API: POST /api/auth/initiate (phoneNumber, password)
 *     API->>+AuthSvc: initiateLogin(request)
 *     AuthSvc->>+UserSvc: findAndValidateCredentials(phone, pass)
 *     UserSvc-->>-AuthSvc: returns User object
 *     AuthSvc->>+OTPSvc: generateAndSendOtp(phone, correlationId)
 *     OTPSvc-->>-AuthSvc: void
 *     AuthSvc-->>-API: returns LoginInitiationResponse
 *     API-->>-C: 200 OK (message, correlationId)
 * }
 * </pre>
 */
@Service
@RequiredArgsConstructor
public class AuthenticationServiceImplLIA_9371 implements IAuthenticationServiceLIA_9371 {

    private final IUserServiceLIA_9371 userService;
    private final IOTPServiceLIA_9371 otpService;
    private final StructuredLoggerLIA_9371 logger;

    /**
     * Orchestrates the login initiation flow: validates credentials, then triggers OTP generation and sending.
     *
     * @param request The login initiation request containing phone number and password.
     * @return A response containing a confirmation message and a correlation ID for the OTP step.
     */
    @Override
    public LoginInitiationResponseLIA_9371 initiateLogin(LoginInitiationRequestLIA_9371 request) {
        long startTime = System.currentTimeMillis();
        logger.logOperationStart("initiateLogin", startTime);

        // 1. Validate user credentials
        UserLIA_9371 user = userService.findAndValidateCredentials(request.getPhoneNumber(), request.getPassword());

        // 2. Generate a unique correlation ID for the OTP verification step
        String correlationId = UUID.randomUUID().toString();
        logger.logInfo("Generated correlation ID", "correlationId", correlationId, "phoneNumber", user.getPhoneNumber());

        // 3. Generate and send the OTP
        otpService.generateAndSendOtp(user.getPhoneNumber(), correlationId);

        // 4. Build and return the successful response
        LoginInitiationResponseLIA_9371 response = LoginInitiationResponseLIA_9371.builder()
                .message("An OTP has been sent to your registered phone number.")
                .correlationId(correlationId)
                .build();

        logger.logOperationEnd("initiateLogin", startTime);
        return response;
    }
}
```
src/main/java/com/example/logininitiation/api/LoginInitiationControllerLIA_9371.java
```java