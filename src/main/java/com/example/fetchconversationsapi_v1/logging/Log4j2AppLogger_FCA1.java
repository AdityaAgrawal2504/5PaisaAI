package com.example.fetchconversationsapi_v1.logging;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class Log4j2AppLogger_FCA1 implements AppLogger_FCA1 {

    private static final Logger logger = LogManager.getLogger(Log4j2AppLogger_FCA1.class);

    @Override
    public void info(String message, Object... args) {
        logger.info(message, args);
    }

    @Override
    public void warn(String message, Object... args) {
        logger.warn(message, args);
    }

    @Override
    public void error(String message, Throwable t, Object... args) {
        logger.error(message, t, args);
    }

    @Override
    public void debug(String message, Object... args) {
        logger.debug(message, args);
    }
}
```
```java
// Logging Aspect for method entry/exit and timing
// File: LoggingAspect_FCA1.java