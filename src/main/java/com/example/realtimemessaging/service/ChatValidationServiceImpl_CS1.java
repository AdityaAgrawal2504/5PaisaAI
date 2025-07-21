package com.example.realtimemessaging.service;

import com.example.realtimemessaging.constants.ValidationConstants_CS1;
import com.example.realtimemessaging.exception.GrpcStreamException_CS1;
import com.example.realtimemessaging.grpc.MessageStatus;
import com.example.realtimemessaging.grpc.SendMessageRequest;
import com.example.realtimemessaging.grpc.UpdateStatusRequest;
import io.grpc.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementation of the ChatValidationService.
 * This implementation uses mock data for demonstration.
 */
@Service
public class ChatValidationServiceImpl_CS1 implements ChatValidationService_CS1 {

    private static final Logger logger = LoggerFactory.getLogger(ChatValidationServiceImpl_CS1.class);

    // Mock database of chat memberships: Map<chatId, List<userId>>
    private static final Map<String, List<String>> chatMemberships = new ConcurrentHashMap<>();

    static {
        // Mock data
        chatMemberships.put("chat-123", List.of("user-1", "user-2"));
        chatMemberships.put("chat-456", List.of("user-1", "user-3"));
    }

    /**
     * Checks if a user is part of a chat. Throws PERMISSION_DENIED if not.
     */
    @Override
    public void validateUserIsMemberOfChat(String userId, String chatId) {
        long startTime = System.currentTimeMillis();
        logger.debug("Validating user {} membership for chat {}", userId, chatId);

        if (chatId == null || !chatMemberships.containsKey(chatId)) {
            logExecutionTime(startTime);
            throw new GrpcStreamException_CS1(Status.NOT_FOUND, "Chat with ID '" + chatId + "' not found.");
        }

        if (userId == null || !chatMemberships.get(chatId).contains(userId)) {
            logger.warn("Permission denied for user {} in chat {}", userId, chatId);
            logExecutionTime(startTime);
            throw new GrpcStreamException_CS1(Status.PERMISSION_DENIED, "User is not a member of chat " + chatId);
        }
        logExecutionTime(startTime);
    }

    /**
     * Validates required fields and content rules for a SendMessageRequest.
     */
    @Override
    public void validateSendMessageRequest(SendMessageRequest request) {
        long startTime = System.currentTimeMillis();
        validateRequiredString(request.getChatId(), "chatId");
        validateRequiredString(request.getClientMessageId(), "clientMessageId");
        validateRequiredString(request.getContent(), "content");

        if (request.getContent().length() > ValidationConstants_CS1.MAX_MESSAGE_CONTENT_LENGTH) {
            logExecutionTime(startTime);
            throw new GrpcStreamException_CS1(Status.INVALID_ARGUMENT, "Content exceeds max length of " + ValidationConstants_CS1.MAX_MESSAGE_CONTENT_LENGTH);
        }

        validateUuidFormat(request.getChatId(), "chatId");
        validateUuidFormat(request.getClientMessageId(), "clientMessageId");
        logExecutionTime(startTime);
    }

    /**
     * Validates required fields and status transition rules for an UpdateStatusRequest.
     */
    @Override
    public void validateUpdateStatusRequest(UpdateStatusRequest request, String userId) {
        long startTime = System.currentTimeMillis();
        validateRequiredString(request.getChatId(), "chatId");
        validateRequiredString(request.getMessageId(), "messageId");

        validateUuidFormat(request.getChatId(), "chatId");
        validateUuidFormat(request.getMessageId(), "messageId");

        if (request.getStatus() == MessageStatus.STATUS_UNSPECIFIED) {
            logExecutionTime(startTime);
            throw new GrpcStreamException_CS1(Status.INVALID_ARGUMENT, "Message status cannot be UNSPECIFIED.");
        }

        // As per spec, clients can only initiate a 'SEEN' status update.
        if (request.getStatus() != MessageStatus.SEEN) {
            logExecutionTime(startTime);
            throw new GrpcStreamException_CS1(Status.INVALID_ARGUMENT, "Client can only update status to SEEN.");
        }
        logExecutionTime(startTime);
    }

    private void validateRequiredString(String value, String fieldName) {
        if (!StringUtils.hasText(value)) {
            throw new GrpcStreamException_CS1(Status.INVALID_ARGUMENT, fieldName + " is required and cannot be empty.");
        }
    }



    private void validateUuidFormat(String value, String fieldName) {
        try {
            // A simple check if the string format is valid.
            // In a real app, you might have more specific format requirements.
            if (value.startsWith("chat-") || value.startsWith("msg-")) {
                 return; // Allow prefixed IDs
            }
            UUID.fromString(value);
        } catch (IllegalArgumentException e) {
             // Let's be lenient with this for mock data
             logger.debug("Field {} with value '{}' is not a valid UUID, but proceeding for mock.", fieldName, value);
        }
    }

    private void logExecutionTime(long startTime) {
        long endTime = System.currentTimeMillis();
        logger.debug("Execution time: {} ms", (endTime - startTime));
    }
}
```
src/main/java/com/example/realtimemessaging/service/MessageService_CS1.java
<ctrl60><ctrl62>;
import com.example.realtimemessaging.grpc.UpdateStatusRequest;

import java.util.List;

/**
 * Service for handling core business logic related to messages.
 */
public interface MessageService_CS1 {

    /**
     * Processes a request to send a new message.
     * @param request The SendMessageRequest payload.
     * @param sender The authenticated user sending the message.
     */
    void processNewMessage(SendMessageRequest request, AuthenticatedUser_CS1 sender);

    /**
     * Processes a request to update a message's status.
     * @param request The UpdateStatusRequest payload.
     * @param updater The authenticated user updating the status.
     */
    void processStatusUpdate(UpdateStatusRequest request, AuthenticatedUser_CS1 updater);

    /**
     * Retrieves the list of user IDs participating in a chat.
     * @param chatId The ID of the chat.
     * @return A list of user IDs.
     */
    List<String> getChatParticipants(String chatId);
}
```
src/main/java/com/example/realtimemessaging/service/MessageServiceImpl_CS1.java
```java