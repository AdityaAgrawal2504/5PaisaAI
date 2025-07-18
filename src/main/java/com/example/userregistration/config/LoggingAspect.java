package com.example.userregistration.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger = LogManager.getLogger(LoggingAspect.class);

    /**
     * Logs the execution time of methods in the service and repository layers.
     * @param point The proceeding join point representing the method call.
     * @return The result of the method execution.
     * @throws Throwable If the invoked method throws an exception.
     */
    @Around("execution(* com.example.userregistration.service..*(..)) || execution(* com.example.userregistration.repository..*(..))")
    public Object logMethodExecutionTime(ProceedingJoinPoint point) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) point.getSignature();
        String className = methodSignature.getDeclaringType().getSimpleName();
        String methodName = methodSignature.getName();

        final StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        logger.trace("Enter: {}.{}()", className, methodName);
        try {
            return point.proceed();
        } finally {
            stopWatch.stop();
            logger.trace("Exit: {}.{}(); Execution time: {} ms", className, methodName, stopWatch.getTotalTimeMillis());
        }
    }
}
```
// src/main/java/com/example/userregistration/enums/ErrorCode.java
```java