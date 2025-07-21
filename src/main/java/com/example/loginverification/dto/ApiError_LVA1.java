package com.example.loginverification.dto;

import com.example.loginverification.model.ErrorCode_LVA1;
import lombok.Getter;

@Getter
public class ApiError_LVA1 {
    private final String errorCode;
    private final String errorMessage;

    public ApiError_LVA1(String errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public ApiError_LVA1(ErrorCode_LVA1 errorCode) {
        this.errorCode = errorCode.getCode();
        this.errorMessage = errorCode.getMessage();
    }
}
```
src/main/java/com/example/loginverification/model/ErrorCode_LVA1.java
```java