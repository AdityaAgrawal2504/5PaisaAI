package com.example.auth.controller;

import com.example.auth.dto.LoginRequest;
import com.example.auth.dto.LoginResponse;
import com.example.auth.service.IAuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for handling user authentication endpoints.
 */
@RestController
@RequiredArgsConstructor
public class LoginController {

    private final IAuthenticationService authenticationService;

    /**
     * Handles the POST request to /login to initiate the user login process.
     * @param loginRequest The request body containing the user's phone number and password.
     * @return A ResponseEntity containing the LoginResponse on success.
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        LoginResponse response = authenticationService.login(loginRequest);
        return ResponseEntity.ok(response);
    }
}

// =================================================================
// RESOURCES (e.g., src/main/resources/log4j2.xml)
// =================================================================
