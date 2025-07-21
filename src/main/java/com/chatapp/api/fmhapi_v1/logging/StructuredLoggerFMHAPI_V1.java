package com.chatapp.api.fmhapi_v1.logging;

import java.util.Map;

/**
 * Abstraction layer for structured logging to support different backends like Log4j2, Kafka, etc.
 */
public interface StructuredLoggerFMHAPI_V1 {

    void logInfo(String event, Map<String, Object> attributes);

    void logError(String event, Map<String, Object> attributes, Throwable t);

    void logMethodStart(String className, String methodName, Map<String, Object> args);

    void logMethodEnd(String className, String methodName, long durationMillis);

    void logMethodEnd(String className, String methodName, long durationMillis, Object result);
}
