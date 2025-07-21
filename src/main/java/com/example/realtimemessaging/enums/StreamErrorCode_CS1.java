package com.example.realtimemessaging.enums;

/**
 * Machine-readable error codes for non-fatal, in-stream error events.
 */
public enum StreamErrorCode_CS1 {
    MESSAGE_SEND_FAILED_BLOCKED,
    MESSAGE_SEND_FAILED_MUTED,
    INVALID_STATUS_UPDATE,
    MALFORMED_REQUEST,
    RATE_LIMIT_EXCEEDED,
    NOT_FOUND,
    INTERNAL_ERROR
}
```
src/main/java/com/example/realtimemessaging/exception/GrpcStreamException_CS1.java
```java