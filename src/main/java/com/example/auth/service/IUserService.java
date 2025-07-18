package com.example.auth.service;

import com.example.auth.model.User;

import java.util.Optional;

/**
 * Manages user data retrieval and updates.
 */
public interface IUserService {
    /**
     * Retrieves a user profile by their phone number.
     * @param phoneNumber The user's phone number.
     * @return An Optional containing the User if found.
     */
    Optional<User> findByPhoneNumber(String phoneNumber);

    /**
     * Compares a plaintext password against a stored hash.
     * @param rawPassword The plaintext password from the login request.
     * @param encodedPassword The stored password hash from the database.
     * @return true if the passwords match, false otherwise.
     */
    boolean verifyPassword(String rawPassword, String encodedPassword);
    
    /**
     * Handles the logic for a failed login attempt for a user.
     * @param user The user who failed to log in.
     */
    void handleFailedLogin(User user);
    
    /**
     * Resets the failed login attempt counter for a user upon successful login.
     * @param user The user who successfully logged in.
     */
    void handleSuccessfulLogin(User user);
}
