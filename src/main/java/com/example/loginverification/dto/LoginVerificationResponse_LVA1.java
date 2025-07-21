package com.example.loginverification.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginVerificationResponse_LVA1 {
    private String authToken;
    private String tokenType;
    private long expiresIn;
}
```
src/main/java/com/example/loginverification/dto/ApiError_LVA1.java
```java