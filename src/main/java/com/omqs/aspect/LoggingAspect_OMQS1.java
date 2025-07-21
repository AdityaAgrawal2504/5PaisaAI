package com.omqs.aspect;

import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Aspect for structured logging of method execution in service and controller layers.
 */
@Aspect
@Component
@Log4j2
public class LoggingAspect_OMQS1 {

    /**
     * Defines a pointcut for all public methods in the controller package.
     */
    @Pointcut("within(com.omqs.controller..*)")
    public void controllerPointcut() {}

    /**
     * Defines a pointcut for all public methods in the service package.
     */
    @Pointcut("within(com.omqs.service..*)")
    public void servicePointcut() {}
    
    /**
     * Defines a pointcut for scheduled tasks.
     */
    @Pointcut("@annotation(org.springframework.scheduling.annotation.Scheduled)")
    public void scheduledTaskPointcut() {}

    /**
     * Logs the entry, exit, and execution time of methods matching the pointcuts.
     */
    @Around("controllerPointcut() || servicePointcut() || scheduledTaskPointcut()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getSignature().getDeclaringType().getSimpleName();
        String methodName = joinPoint.getSignature().getName();

        log.info("Enter: {}.{}() with argument[s] = {}", className, methodName, Arrays.toString(joinPoint.getArgs()));
        long startTime = System.currentTimeMillis();

        try {
            Object result = joinPoint.proceed();
            long endTime = System.currentTimeMillis();
            long executionTime = endTime - startTime;
            log.info("Exit: {}.{}(). Execution time: {} ms. Result: {}", className, methodName, executionTime, result);
            return result;
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            long executionTime = endTime - startTime;
            log.error("Exception in {}.{}() with cause = '{}'. Execution time: {} ms.",
                    className, methodName, e.getCause() != null ? e.getCause() : "NULL", executionTime);
            throw e;
        }
    }
}
```
```java
// src/main/java/com/omqs/service/OfflineMessageService_OMQS1.java