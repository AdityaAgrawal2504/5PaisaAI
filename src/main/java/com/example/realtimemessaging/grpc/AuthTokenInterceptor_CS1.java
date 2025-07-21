package com.example.realtimemessaging.grpc;

import com.example.realtimemessaging.constants.GrpcConstants_CS1;
import com.example.realtimemessaging.domain.AuthenticatedUser_CS1;
import com.example.realtimemessaging.service.AuthenticationService_CS1;
import io.grpc.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

/**
 * gRPC interceptor to handle JWT authentication for all incoming RPC calls.
 */
@Component
public class AuthTokenInterceptor_CS1 implements ServerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(AuthTokenInterceptor_CS1.class);
    private final AuthenticationService_CS1 authenticationService;

    public AuthTokenInterceptor_CS1(AuthenticationService_CS1 authenticationService) {
        this.authenticationService = authenticationService;
    }

    /**
     * Intercepts an incoming call to validate the authentication token from metadata.
     */
    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> call,
            Metadata headers,
            ServerCallHandler<ReqT, RespT> next) {

        String token = headers.get(GrpcConstants_CS1.AUTHORIZATION_METADATA_KEY);

        try {
            AuthenticatedUser_CS1 user = authenticationService.authenticate(token)
                    .orElseThrow(() -> Status.UNAUTHENTICATED.withDescription("Invalid or missing authentication token.").asRuntimeException());

            // Add user info and other context to the logging MDC (Mapped Diagnostic Context)
            MDC.put("userId", user.getUserId());

            // Store the authenticated user in the gRPC context for access in the service implementation
            Context context = Context.current().withValue(GrpcConstants_CS1.AUTHENTICATED_USER_CONTEXT_KEY, user);
            
            // Proceed with the call in the new context
            return Contexts.interceptCall(context, call, headers, next);

        } catch (StatusRuntimeException e) {
            logger.warn("Authentication check failed: {}", e.getStatus().getDescription());
            call.close(e.getStatus(), new Metadata());
            return new ServerCall.Listener<>() {}; // No-op listener
        } finally {
            // Clear the MDC to prevent context leakage to other threads
             MDC.clear();
        }
    }
}
```
src/main/java/com/example/realtimemessaging/config/GrpcSecurityConfig_CS1.java
<ctrl60>```java