package com.example.userregistration.controller;

import com.example.userregistration.dto.UserRegistrationRequest;
import com.example.userregistration.dto.UserRegistrationResponse;
import com.example.userregistration.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@RestController
public class UserRegistrationController {

    private static final Logger logger = LogManager.getLogger(UserRegistrationController.class);
    private final UserService userService;

    public UserRegistrationController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Handles the HTTP POST request to register a new user.
     * @param request The request body containing user credentials, validated against defined rules.
     * @return A ResponseEntity with the registration response and a 201 CREATED status on success.
     */
    @PostMapping("/register")
    public ResponseEntity<UserRegistrationResponse> register(@Valid @RequestBody UserRegistrationRequest request) {
        logger.info("Received registration request for phone number: {}", request.getPhoneNumber());
        UserRegistrationResponse response = userService.registerUser(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
```