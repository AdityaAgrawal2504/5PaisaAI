package com.omqs.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.util.Map;

/**
 * Converts a Map<String, Object> to its JSON string representation for persistence
 * and back again.
 */
@Converter
@Log4j2
@RequiredArgsConstructor
public class PayloadConverter_OMQS1 implements AttributeConverter<Map<String, Object>, String> {

    private final ObjectMapper objectMapper;

    /**
     * Converts the map attribute into a JSON string for database storage.
     */
    @Override
    public String convertToDatabaseColumn(Map<String, Object> attribute) {
        if (attribute == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize payload to JSON string", e);
            throw new IllegalArgumentException("Error converting payload to JSON", e);
        }
    }

    /**
     * Converts the JSON string from the database back into a map attribute.
     */
    @Override
    public Map<String, Object> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) {
            return null;
        }
        try {
            return objectMapper.readValue(dbData, new TypeReference<>() {});
        } catch (IOException e) {
            log.error("Failed to deserialize JSON string to payload map", e);
            throw new IllegalArgumentException("Error converting JSON to payload", e);
        }
    }
}
```
```java
// src/main/java/com/omqs/model/OfflineMessage_OMQS1.java