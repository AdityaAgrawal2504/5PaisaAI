package com.example.realtimemessaging.service;

import com.example.realtimemessaging.grpc.SendMessageRequest;
import com.example.realtimemessaging.grpc.UpdateStatusRequest;

/**
 * Service for validating chat-related requests and user permissions.
 */
public interface ChatValidationService_CS1 {

    /**
     * Validates if a user is a member of a given chat.
     * @param userId The user's ID.
     * @param chatId The chat's ID.
     */
    void validateUserIsMemberOfChat(String userId, String chatId);

    /**
     * Validates the content and structure of a SendMessageRequest.
     * @param request The request to validate.
     */
    void validateSendMessageRequest(SendMessageRequest request);

    /**
     * Validates the content and structure of an UpdateStatusRequest.
     * Also checks if the requested status transition is allowed.
     * @param request The request to validate.
     * @param userId The ID of the user making the request.
     */
    void validateUpdateStatusRequest(UpdateStatusRequest request, String userId);

}
```
src/main/java/com/example/realtimemessaging/service/ChatValidationServiceImpl_CS1.java
```java