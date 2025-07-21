package com.example.realtimemessaging.constants;

import io.grpc.Context;
import io.grpc.Metadata;

import com.example.realtimemessaging.domain.AuthenticatedUser_CS1;

/**
 * Holds constant values used across the gRPC layer.
 */
public final class GrpcConstants_CS1 {

    private GrpcConstants_CS1() {
    }

    public static final Metadata.Key<String> AUTHORIZATION_METADATA_KEY =
            Metadata.Key.of("authorization", Metadata.ASCII_STRING_MARSHALLER);

    public static final Context.Key<AuthenticatedUser_CS1> AUTHENTICATED_USER_CONTEXT_KEY =
            Context.key("authenticatedUser");
}
```
src/main/java/com/example/realtimemessaging/constants/ValidationConstants_CS1.java
```java