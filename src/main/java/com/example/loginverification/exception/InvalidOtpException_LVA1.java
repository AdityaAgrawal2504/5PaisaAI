package com.example.loginverification.exception;

import com.example.loginverification.model.ErrorCode_LVA1;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class InvalidOtpException_LVA1 extends ApiException_LVA1 {
    public InvalidOtpException_LVA1() {
        super(ErrorCode_LVA1.INVALID_OTP);
    }
}
```
src/main/java/com/example/loginverification/exception/OtpExpiredException_LVA1.java
```java