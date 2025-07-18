package com.example.auth.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * Represents a user entity in the system.
 */
@Data
@Entity
@Table(name = "app_user")
public class User {

    /**
     * Unique identifier for the user (e.g., UUID).
     */
    @Id
    private String id;

    /**
     * The user's unique, verified phone number.
     */
    private String phoneNumber;

    /**
     * Hashed and salted password.
     */
    private String passwordHash;

    /**
     * Counter for consecutive failed login attempts.
     */
    private int failedLoginAttempts;

    /**
     * Flag indicating if the account is locked.
     */
    private boolean isLocked;

    /**
     * Timestamp until which the account is locked.
     */
    private LocalDateTime lockoutEndDate;
}


// =================================================================
// REPOSITORY
// =================================================================
