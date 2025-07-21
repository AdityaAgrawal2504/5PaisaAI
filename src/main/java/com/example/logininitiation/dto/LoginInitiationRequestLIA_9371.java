package com.example.logininitiation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for the login initiation request body.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginInitiationRequestLIA_9371 {

    /**
     * The user's 10-digit phone number.
     */
    @NotBlank(message = "Phone number must not be blank.")
    @Pattern(regexp = "^\\d{10}$", message = "Phone number must be exactly 10 digits.")
    private String phoneNumber;

    /**
     * The user's plaintext password for authentication.
     */
    @NotBlank(message = "Password must not be blank.")
    @Size(min = 8, max = 128, message = "Password must be between 8 and 128 characters.")
    private String password;
}
```
src/main/java/com/example/logininitiation/dto/LoginInitiationResponseLIA_9371.java
```java