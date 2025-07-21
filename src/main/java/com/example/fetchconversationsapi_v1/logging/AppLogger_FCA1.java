package com.example.fetchconversationsapi_v1.logging;

public interface AppLogger_FCA1 {
    void info(String message, Object... args);
    void warn(String message, Object... args);
    void error(String message, Throwable t, Object... args);
    void debug(String message, Object... args);
}
```
```java
// Log4j2 Logger Implementation
// File: Log4j2AppLogger_FCA1.java