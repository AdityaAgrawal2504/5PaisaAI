package com.example.loginverification.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginVerificationRequest_LVA1 {

    @NotBlank(message = "Phone number cannot be blank.")
    @Pattern(regexp = "^\\+[1-9]\\d{1,14}$", message = "Phone number must be in E.164 format.")
    private String phoneNumber;

    @NotBlank(message = "OTP cannot be blank.")
    @Size(min = 6, max = 6, message = "OTP must be exactly 6 digits.")
    @Pattern(regexp = "^[0-9]{6}$", message = "OTP must consist of 6 numeric digits.")
    private String otp;
}
```
src/main/java/com/example/loginverification/dto/LoginVerificationResponse_LVA1.java
```java