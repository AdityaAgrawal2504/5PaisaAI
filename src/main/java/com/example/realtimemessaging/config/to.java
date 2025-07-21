package com.example.realtimemessaging.config;

import com.example.realtimemessaging.grpc.AuthTokenInterceptor_CS1;
import net.devh.boot.grpc.server.interceptor.GrpcGlobalServerInterceptor;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class to register gRPC server interceptors globally.
 */
@Configuration
public class GrpcSecurityConfig_CS1 {

    /**
     * Makes the AuthTokenInterceptor a global interceptor for all gRPC services.
     * @param authTokenInterceptor The authentication interceptor bean.
     * @return The configured interceptor.
     */
    @GrpcGlobalServerInterceptor
    public AuthTokenInterceptor_CS1 authTokenInterceptor(AuthTokenInterceptor_CS1 authTokenInterceptor) {
        return authTokenInterceptor;
    }
}
```
src/main/java/com/example/realtimemessaging/handler/StreamLifecycleHandler_CS1.java
```java