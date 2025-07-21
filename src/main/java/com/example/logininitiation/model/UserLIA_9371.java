package com.example.logininitiation.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * JPA Entity representing a user in the system.
 */
@Entity
@Table(name = "users")
@Data
public class UserLIA_9371 {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String phoneNumber;
    private String passwordHash;
    private boolean accountLocked = false;
    private boolean enabled = true;
}
```
src/main/java/com/example/logininitiation/repository/UserRepositoryLIA_9371.java
```java