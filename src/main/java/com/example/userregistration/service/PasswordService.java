package com.example.userregistration.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class PasswordService {
    private final PasswordEncoder passwordEncoder;

    public PasswordService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Hashes a plain-text password using the configured password encoder.
     * @param plainTextPassword The password to hash.
     * @return The resulting password hash as a string.
     */
    public String hashPassword(String plainTextPassword) {
        return passwordEncoder.encode(plainTextPassword);
    }
}
```
// src/main/java/com/example/userregistration/service/UserService.java
```java