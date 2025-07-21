package com.example.loginverification.exception;

import com.example.loginverification.model.ErrorCode_LVA1;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ApiException_LVA1 extends RuntimeException {
    private final ErrorCode_LVA1 errorCode;
    private final HttpStatus httpStatus;

    public ApiException_LVA1(ErrorCode_LVA1 errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.httpStatus = errorCode.getStatus();
    }

    public ApiException_LVA1(ErrorCode_LVA1 errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = errorCode.getStatus();
    }
}
```
src/main/java/com/example/loginverification/exception/ResourceNotFoundException_LVA1.java
```java