package com.example.logininitiation.api;

import com.example.logininitiation.dto.LoginInitiationRequestLIA_9371;
import com.example.logininitiation.dto.LoginInitiationResponseLIA_9371;
import com.example.logininitiation.service.IAuthenticationServiceLIA_9371;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller for handling the Login Initiation API endpoint.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class LoginInitiationControllerLIA_9371 {

    private final IAuthenticationServiceLIA_9371 authenticationService;

    /**
     * Initiates the login process by validating user credentials and sending an OTP.
     *
     * @param request The request body containing the user's phone number and password.
     * @return A ResponseEntity with the result of the login initiation.
     */
    @PostMapping("/initiate")
    public ResponseEntity<LoginInitiationResponseLIA_9371> initiateLogin(
            @Valid @RequestBody LoginInitiationRequestLIA_9371 request) {

        LoginInitiationResponseLIA_9371 response = authenticationService.initiateLogin(request);
        return ResponseEntity.ok(response);
    }
}
```
src/test/java/com/example/logininitiation/service/OtpServiceImplLIA_9371Test.java
```java