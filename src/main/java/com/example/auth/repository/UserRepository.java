package com.example.auth.repository;

import com.example.auth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for the User entity.
 */
@Repository
public interface UserRepository extends JpaRepository<User, String> {

    /**
     * Finds a user by their phone number.
     * @param phoneNumber The phone number to search for.
     * @return An Optional containing the User if found, otherwise empty.
     */
    Optional<User> findByPhoneNumber(String phoneNumber);
}

// =================================================================
// EXCEPTIONS
// =================================================================
