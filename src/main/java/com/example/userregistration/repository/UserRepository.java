package com.example.userregistration.repository;

import com.example.userregistration.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    /**
     * Finds a user by their unique phone number.
     * @param phoneNumber The 10-digit phone number to search for.
     * @return An Optional containing the User if found, otherwise an empty Optional.
     */
    Optional<User> findByPhoneNumber(String phoneNumber);
}
```
// src/main/java/com/example/userregistration/service/PasswordService.java
```java