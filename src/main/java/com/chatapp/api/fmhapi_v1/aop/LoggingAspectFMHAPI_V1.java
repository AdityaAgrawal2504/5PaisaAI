package com.chatapp.api.fmhapi_v1.aop;

import com.chatapp.api.fmhapi_v1.logging.StructuredLoggerFMHAPI_V1;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.Map;

/**
 * Aspect for logging method execution time and parameters for controllers and services.
 */
@Aspect
@Component
public class LoggingAspectFMHAPI_V1 {

    private final StructuredLoggerFMHAPI_V1 structuredLogger;

    public LoggingAspectFMHAPI_V1(StructuredLoggerFMHAPI_V1 structuredLogger) {
        this.structuredLogger = structuredLogger;
    }

    /**
     * Logs execution of methods in service and controller packages.
     * <!--
     * mermaid
     * sequenceDiagram
     *   participant Client
     *   participant A as LoggingAspect
     *   participant T as TargetMethod
     *   Client->>A: invokeMethod()
     *   activate A
     *   A->>T: logMethodStart()
     *   A->>T: proceed()
     *   activate T
     *   T-->>A: return result
     *   deactivate T
     *   A->>T: logMethodEnd()
     *   A-->>Client: return result
     *   deactivate A
     * -->
     */
    @Around("execution(* com.chatapp.api.fmhapi_v1.service..*.*(..)) || execution(* com.chatapp.api.fmhapi_v1.controller..*.*(..))")
    public Object logMethodExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String className = signature.getDeclaringType().getSimpleName();
        String methodName = signature.getName();

        Map<String, Object> args = new HashMap<>();
        String[] parameterNames = signature.getParameterNames();
        Object[] parameterValues = joinPoint.getArgs();
        for (int i = 0; i < parameterNames.length; i++) {
             // Avoid logging sensitive or large objects
            if (parameterValues[i] != null && !(parameterValues[i].getClass().getName().contains("HttpServlet"))) {
                 args.put(parameterNames[i], parameterValues[i].toString());
            }
        }

        structuredLogger.logMethodStart(className, methodName, args);

        long startTime = System.currentTimeMillis();
        Object result;
        try {
            result = joinPoint.proceed();
            long duration = System.currentTimeMillis() - startTime;
            structuredLogger.logMethodEnd(className, methodName, duration, result);
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            Map<String, Object> errorAttributes = new HashMap<>();
            errorAttributes.put("class", className);
            errorAttributes.put("method", methodName);
            errorAttributes.put("durationMs", duration);
            structuredLogger.logError("method_exception", errorAttributes, e);
            throw e;
        }

        return result;
    }
}
