package com.example.userregistration.service;

import com.example.userregistration.dto.UserRegistrationRequest;
import com.example.userregistration.dto.UserRegistrationResponse;
import com.example.userregistration.entity.User;
import com.example.userregistration.exception.UserAlreadyExistsException;
import com.example.userregistration.repository.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/*
 * mermaid
 * sequenceDiagram
 *   participant C as Client
 *   participant Ctrl as UserRegistrationController
 *   participant Svc as UserService
 *   participant PwdSvc as PasswordService
 *   participant Repo as UserRepository
 *   participant DB as Database
 *
 *   C->>+Ctrl: POST /register (UserRegistrationRequest)
 *   Ctrl->>+Svc: registerUser(request)
 *   Svc->>+Repo: findByPhoneNumber(phoneNumber)
 *   Repo->>+DB: SELECT * FROM app_user WHERE phone_number = ?
 *   DB-->>-Repo: User record (or null)
 *   alt User Exists
 *     Repo-->>Svc: Optional.of(User)
 *     Svc-->>-Svc: throw UserAlreadyExistsException
 *     Svc-->>-Ctrl: UserAlreadyExistsException
 *     Ctrl-->>-C: 409 Conflict (ErrorResponse)
 *   else User Does Not Exist
 *     Repo-->>Svc: Optional.empty()
 *     Svc->>+PwdSvc: hashPassword(plainTextPassword)
 *     PwdSvc-->>-Svc: hashedPassword
 *     Svc->>Svc: Create User entity
 *     Svc->>+Repo: save(newUser)
 *     Repo->>+DB: INSERT INTO app_user (...)
 *     DB-->>-Repo: Saved User with ID and timestamp
 *     Repo-->>-Svc: Saved User entity
 *     Svc->>Svc: Create UserRegistrationResponse
 *     Svc-->>-Ctrl: UserRegistrationResponse
 *     Ctrl-->>-C: 201 Created (UserRegistrationResponse)
 *   end
 */
@Service
public class UserService {

    private static final Logger logger = LogManager.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final PasswordService passwordService;

    public UserService(UserRepository userRepository, PasswordService passwordService) {
        this.userRepository = userRepository;
        this.passwordService = passwordService;
    }

    /**
     * Registers a new user based on the provided registration data.
     * @param request The user registration request DTO containing phone number and password.
     * @return A DTO containing details of the successfully created user.
     * @throws UserAlreadyExistsException if a user with the phone number already exists.
     */
    @Transactional
    public UserRegistrationResponse registerUser(UserRegistrationRequest request) {
        logger.info("Attempting to register user with phone number: {}", request.getPhoneNumber());

        userRepository.findByPhoneNumber(request.getPhoneNumber()).ifPresent(user -> {
            throw new UserAlreadyExistsException(request.getPhoneNumber());
        });

        String hashedPassword = passwordService.hashPassword(request.getPassword());

        User newUser = new User();
        newUser.setPhoneNumber(request.getPhoneNumber());
        newUser.setPasswordHash(hashedPassword);

        User savedUser = userRepository.save(newUser);
        logger.info("Successfully registered user with ID: {}", savedUser.getId());

        return UserRegistrationResponse.builder()
                .userId(savedUser.getId())
                .message("User registered successfully.")
                .createdAt(savedUser.getCreatedAt())
                .build();
    }
}
```
// src/main/java/com/example/userregistration/controller/UserRegistrationController.java
```java