package com.example.auth.util.logging;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * AOP Aspect to intercept methods annotated with @Loggable for structured performance logging.
 */
@Aspect
@Component
public class LoggingAspect {

    private static final Logger log = LoggerFactory.getLogger(LoggingAspect.class);

    /**
     * Advice that wraps around methods annotated with @Loggable.
     * Logs the start, end, and execution time of the method call.
     * @param joinPoint The join point for the advised method.
     * @return The result of the original method call.
     * @throws Throwable if the intercepted method throws an exception.
     */
    @Around("@annotation(com.example.auth.util.logging.Loggable)")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        String methodName = joinPoint.getSignature().toShortString();
        log.info("Enter: {}. Args: {}", methodName, joinPoint.getArgs());

        Object result;
        try {
            result = joinPoint.proceed();
        } catch (Throwable throwable) {
            log.error("Exception in {}: {}", methodName, throwable.getMessage());
            throw throwable;
        }

        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;

        log.info("Exit: {}. Execution time: {}ms. Result: {}", methodName, executionTime, result);
        return result;
    }
}

// =================================================================
// SERVICE INTERFACES
// =================================================================
