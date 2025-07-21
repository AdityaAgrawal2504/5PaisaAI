package com.example.loginverification.exception;

import com.example.loginverification.model.ErrorCode_LVA1;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
public class RateLimitException_LVA1 extends ApiException_LVA1 {
    public RateLimitException_LVA1() {
        super(ErrorCode_LVA1.TOO_MANY_ATTEMPTS);
    }
}
```
src/main/java/com/example/loginverification/exception/GlobalExceptionHandler_LVA1.java
```java