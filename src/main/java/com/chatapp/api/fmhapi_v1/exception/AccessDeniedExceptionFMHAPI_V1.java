package com.chatapp.api.fmhapi_v1.exception;

import com.chatapp.api.fmhapi_v1.model.enums.ErrorCodeFMHAPI_V1;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Custom exception for access denied errors (403 Forbidden).
 */
@Getter
public class AccessDeniedExceptionFMHAPI_V1 extends RuntimeException {
    private final ErrorCodeFMHAPI_V1 errorCode = ErrorCodeFMHAPI_V1.ACCESS_DENIED;
    private final HttpStatus httpStatus = HttpStatus.FORBIDDEN;

    public AccessDeniedExceptionFMHAPI_V1(String message) {
        super(message);
    }
}
