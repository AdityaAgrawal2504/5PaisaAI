package com.chatapp.api.fmhapi_v1.exception;

import com.chatapp.api.fmhapi_v1.model.enums.ErrorCodeFMHAPI_V1;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Custom exception for invalid cursor errors (400 Bad Request).
 */
@Getter
public class InvalidCursorExceptionFMHAPI_V1 extends RuntimeException {
    private final ErrorCodeFMHAPI_V1 errorCode = ErrorCodeFMHAPI_V1.INVALID_CURSOR;
    private final HttpStatus httpStatus = HttpStatus.BAD_REQUEST;

    public InvalidCursorExceptionFMHAPI_V1(String message) {
        super(message);
    }
}
