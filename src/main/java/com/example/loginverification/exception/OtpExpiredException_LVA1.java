package com.example.loginverification.exception;

import com.example.loginverification.model.ErrorCode_LVA1;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class OtpExpiredException_LVA1 extends ApiException_LVA1 {
    public OtpExpiredException_LVA1() {
        super(ErrorCode_LVA1.OTP_EXPIRED);
    }
}
```
src/main/java/com/example/loginverification/exception/RateLimitException_LVA1.java
```java