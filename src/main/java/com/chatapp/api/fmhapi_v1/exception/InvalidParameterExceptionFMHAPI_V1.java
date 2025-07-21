package com.chatapp.api.fmhapi_v1.exception;

import com.chatapp.api.fmhapi_v1.model.enums.ErrorCodeFMHAPI_V1;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Map;

/**
 * Custom exception for invalid parameter errors (400 Bad Request).
 */
@Getter
public class InvalidParameterExceptionFMHAPI_V1 extends RuntimeException {
    private final ErrorCodeFMHAPI_V1 errorCode = ErrorCodeFMHAPI_V1.INVALID_QUERY_PARAMETER;
    private final HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
    private final Map<String, Object> details;

    public InvalidParameterExceptionFMHAPI_V1(String message, Map<String, Object> details) {
        super(message);
        this.details = details;
    }
}
