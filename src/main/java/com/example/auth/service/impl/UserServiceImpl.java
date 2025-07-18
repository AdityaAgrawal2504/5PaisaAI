package com.example.auth.service.impl;

import com.example.auth.model.User;
import com.example.auth.repository.UserRepository;
import com.example.auth.service.IUserService;
import com.example.auth.util.logging.Loggable;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Implementation of IUserService for managing user data and login attempt logic.
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {

    @Value("${app.security.max-failed-attempts:5}")
    private int MAX_FAILED_ATTEMPTS;

    @Value("${app.security.lockout-duration-minutes:15}")
    private int LOCKOUT_DURATION_MINUTES;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Finds a user by their registered phone number.
     * @param phoneNumber The user's phone number.
     * @return An Optional containing the User if found.
     */
    @Override
    @Loggable
    @Transactional(readOnly = true)
    public Optional<User> findByPhoneNumber(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber);
    }

    /**
     * Verifies if a raw password matches the user's stored encoded password.
     * @param rawPassword The plaintext password from the request.
     * @param encodedPassword The stored hashed password.
     * @return true if passwords match, false otherwise.
     */
    @Override
    @Loggable
    public boolean verifyPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    /**
     * Increments the failed login attempt counter. If the threshold is reached, locks the account.
     * @param user The user who failed to log in.
     */
    @Override
    @Loggable
    @Transactional
    public void handleFailedLogin(User user) {
        user.setFailedLoginAttempts(user.getFailedLoginAttempts() + 1);
        if (user.getFailedLoginAttempts() >= MAX_FAILED_ATTEMPTS) {
            user.setLocked(true);
            user.setLockoutEndDate(LocalDateTime.now().plusMinutes(LOCKOUT_DURATION_MINUTES));
        }
        userRepository.save(user);
    }

    /**
     * Resets the failed login attempt counter and unlocks the account if it was locked.
     * @param user The user who successfully logged in.
     */
    @Override
    @Loggable
    @Transactional
    public void handleSuccessfulLogin(User user) {
        user.setFailedLoginAttempts(0);
        user.setLocked(false);
        user.setLockoutEndDate(null);
        userRepository.save(user);
    }
}
