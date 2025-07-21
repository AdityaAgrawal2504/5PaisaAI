package com.example.loginverification.logging;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class Log4j2LoggingService_LVA1 implements LoggingService_LVA1 {

    private static final Logger logger = LogManager.getLogger(Log4j2LoggingService_LVA1.class);

    @Override
    public void logInfo(String message, Object... params) {
        logger.info(message, params);
    }

    @Override
    public void logWarn(String message, Object... params) {
        logger.warn(message, params);
    }

    @Override
    public void logError(String message, Throwable t, Object... params) {
        logger.error(message, params, t);
    }

    @Override
    public void logDebug(String message, Object... params) {
        logger.debug(message, params);
    }

    @Override
    public long logFunctionStart(String functionName) {
        long startTime = System.nanoTime();
        logger.info("Function START: {}", functionName);
        return startTime;
    }

    @Override
    public void logFunctionEnd(String functionName, long startTime) {
        long endTime = System.nanoTime();
        long durationMs = TimeUnit.NANOSECONDS.toMillis(endTime - startTime);
        logger.info("Function END: {}. Duration: {}ms", functionName, durationMs);
    }
}
```
src/main/java/com/example/loginverification/util/JwtTokenUtil_LVA1.java
```java