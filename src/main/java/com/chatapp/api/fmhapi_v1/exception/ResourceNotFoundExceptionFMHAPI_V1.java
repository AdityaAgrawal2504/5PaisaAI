package com.chatapp.api.fmhapi_v1.exception;

import com.chatapp.api.fmhapi_v1.model.enums.ErrorCodeFMHAPI_V1;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Custom exception for resource not found errors (404 Not Found).
 */
@Getter
public class ResourceNotFoundExceptionFMHAPI_V1 extends RuntimeException {
    private final ErrorCodeFMHAPI_V1 errorCode;
    private final HttpStatus httpStatus = HttpStatus.NOT_FOUND;

    public ResourceNotFoundExceptionFMHAPI_V1(ErrorCodeFMHAPI_V1 errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}
