package com.example.realtimemessaging.exception;

import io.grpc.Status;

/**
 * Custom exception that can be mapped to a gRPC Status to terminate a stream with a specific error.
 */
public class GrpcStreamException_CS1 extends RuntimeException {

    private final Status status;

    public GrpcStreamException_CS1(Status status, String message) {
        super(message);
        this.status = status.withDescription(message);
    }

    public GrpcStreamException_CS1(Status status, String message, Throwable cause) {
        super(message, cause);
        this.status = status.withDescription(message).withCause(cause);
    }

    public Status getStatus() {
        return status;
    }
}
```
src/main/java/com/example/realtimemessaging/service/AuthenticationService_CS1.java
```java