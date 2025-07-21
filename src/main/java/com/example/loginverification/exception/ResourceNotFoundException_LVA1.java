package com.example.loginverification.exception;

import com.example.loginverification.model.ErrorCode_LVA1;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException_LVA1 extends ApiException_LVA1 {
    public ResourceNotFoundException_LVA1() {
        super(ErrorCode_LVA1.USER_NOT_FOUND);
    }
}
```
src/main/java/com/example/loginverification/exception/InvalidOtpException_LVA1.java
```java