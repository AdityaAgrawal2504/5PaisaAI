package com.example.logininitiation.config;

import com.example.logininitiation.util.RequestLoggingInterceptorLIA_9371;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configures Spring MVC, including adding custom interceptors.
 */
@Configuration
@RequiredArgsConstructor
public class WebMvcConfigLIA_9371 implements WebMvcConfigurer {

    private final RequestLoggingInterceptorLIA_9371 requestLoggingInterceptor;

    /**
     * Registers the custom interceptor to process all incoming requests.
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(requestLoggingInterceptor);
    }
}
```
src/main/java/com/example/logininitiation/config/SecurityConfigLIA_9371.java
```java