package com.example.fetchconversationsapi_v1.logging;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;

@Aspect
@Component
public class LoggingAspect_FCA1 {

    private final AppLogger_FCA1 logger;
    private final ObjectMapper objectMapper;

    public LoggingAspect_FCA1(AppLogger_FCA1 logger) {
        this.logger = logger;
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Defines a pointcut for methods within classes annotated with @RestController.
     */
    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void controllerPointcut() {}

    /**
     * Defines a pointcut for methods within classes annotated with @Service.
     */
    @Pointcut("within(@org.springframework.stereotype.Service *)")
    public void servicePointcut() {}

    /**
     * Advises methods matching the pointcuts, logging entry, exit, and execution time.
     */
    @Around("controllerPointcut() || servicePointcut()")
    public Object logAround(ProceedingJoinPoin t joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();

        try {
            String args = objectMapper.writeValueAsString(joinPoint.getArgs());
            logger.info("Enter: {}.{}() with argument[s] = {}", className, methodName, args);
        } catch (Exception e) {
             logger.info("Enter: {}.{}()", className, methodName);
        }

        Object result = joinPoint.proceed();
        long endTime = System.currentTimeMillis();
        long timeTaken = endTime - startTime;

        try {
            String returnValue = objectMapper.writeValueAsString(result);
            logger.info("Exit: {}.{}() with result = {}. Execution time: {} ms", className, methodName, returnValue, timeTaken);
        } catch (Exception e) {
            logger.info("Exit: {}.{}(). Execution time: {} ms", className, methodName, timeTaken);
        }
        
        return result;
    }
}
```
```java
// Sort By Enum
// File: ConversationSortBy_FCA1.java