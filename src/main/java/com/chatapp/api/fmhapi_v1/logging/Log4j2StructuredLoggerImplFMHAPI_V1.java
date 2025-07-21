package com.chatapp.api.fmhapi_v1.logging;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.MapMessage;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Log4j2 implementation of the structured logging interface.
 */
@Component
public class Log4j2StructuredLoggerImplFMHAPI_V1 implements StructuredLoggerFMHAPI_V1 {

    private static final Logger logger = LogManager.getLogger(Log4j2StructuredLoggerImplFMHAPI_V1.class);

    @Override
    public void logInfo(String event, Map<String, Object> attributes) {
        MapMessage mapMessage = new MapMessage(attributes);
        mapMessage.put("event", event);
        logger.info(mapMessage);
    }

    @Override
    public void logError(String event, Map<String, Object> attributes, Throwable t) {
        MapMessage mapMessage = new MapMessage(attributes);
        mapMessage.put("event", event);
        mapMessage.put("errorMessage", t.getMessage());
        logger.error(mapMessage, t);
    }

    @Override
    public void logMethodStart(String className, String methodName, Map<String, Object> args) {
        Map<String, Object> logData = new HashMap<>();
        logData.put("class", className);
        logData.put("method", methodName);
        logData.put("phase", "start");
        if (args != null && !args.isEmpty()) {
            logData.put("args", args);
        }
        logInfo("method_execution", logData);
    }

    @Override
    public void logMethodEnd(String className, String methodName, long durationMillis) {
        Map<String, Object> logData = new HashMap<>();
        logData.put("class", className);
        logData.put("method", methodName);
        logData.put("phase", "end");
        logData.put("durationMs", durationMillis);
        logInfo("method_execution", logData);
    }
    
    @Override
    public void logMethodEnd(String className, String methodName, long durationMillis, Object result) {
         Map<String, Object> logData = new HashMap<>();
        logData.put("class", className);
        logData.put("method", methodName);
        logData.put("phase", "end");
        logData.put("durationMs", durationMillis);
        // Avoid logging large results
        if (result != null) {
            logData.put("result_type", result.getClass().getSimpleName());
        }
        logInfo("method_execution", logData);
    }
}
