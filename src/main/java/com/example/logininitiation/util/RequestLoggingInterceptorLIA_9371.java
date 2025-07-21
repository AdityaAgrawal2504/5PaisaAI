package com.example.logininitiation.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.UUID;

/**
 * Interceptor to manage the X-Request-ID for logging and traceability.
 * It adds the request ID to the SLF4J Mapped Diagnostic Context (MDC).
 */
@Component
public class RequestLoggingInterceptorLIA_9371 implements HandlerInterceptor {

    private static final String REQUEST_ID_HEADER = "X-Request-ID";
    private static final String MDC_KEY = "requestId";

    /**
     * Before handling the request, extract the X-Request-ID header or generate a new one.
     * The ID is then placed into the MDC for inclusion in all log statements for this thread.
     */
    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) {
        String requestId = request.getHeader(REQUEST_ID_HEADER);
        if (requestId == null || requestId.isBlank()) {
            requestId = UUID.randomUUID().toString();
        }
        MDC.put(MDC_KEY, requestId);
        return true;
    }

    /**
     * After the request is completed, clean up the MDC to prevent memory leaks in the thread pool.
     */
    @Override
    public void afterCompletion(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler, Exception ex) {
        MDC.remove(MDC_KEY);
    }
}
```
src/main/java/com/example/logininitiation/config/WebMvcConfigLIA_9371.java
```java