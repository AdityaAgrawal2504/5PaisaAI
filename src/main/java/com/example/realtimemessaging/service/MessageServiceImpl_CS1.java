package com.example.realtimemessaging.service;

import com.example.realtimemessaging.domain.AuthenticatedUser_CS1;
import com.example.realtimemessaging.grpc.*;
import com.example.realtimemessaging.util.IdGenerator_CS1;
import com.example.realtimemessaging.util.TimestampConverter_CS1;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Mock implementation of MessageService.
 * In a real application, this service would interact with a persistent database.
 */
@Service
public class MessageServiceImpl_CS1 implements MessageService_CS1 {

    private static final Logger logger = LoggerFactory.getLogger(MessageServiceImpl_CS1.class);

    private final EventBroadcasterService_CS1 eventBroadcaster;
    private final IdGenerator_CS1 idGenerator;

    // Mock data stores
    private static final Map<String, List<String>> chatMemberships = new ConcurrentHashMap<>();
    // In a real app, this would be a repository call
    static {
        chatMemberships.put("chat-123", List.of("user-1", "user-2"));
        chatMemberships.put("chat-456", List.of("user-1", "user-3"));
    }

    public MessageServiceImpl_CS1(EventBroadcasterService_CS1 eventBroadcaster, IdGenerator_CS1 idGenerator) {
        this.eventBroadcaster = eventBroadcaster;
        this.idGenerator = idGenerator;
    }

    /**
     * Creates a NewMessageEvent and broadcasts it to chat participants.
     */
    @Override
    public void processNewMessage(SendMessageRequest request, AuthenticatedUser_CS1 sender) {
        long startTime = System.currentTimeMillis();
        logger.info("Processing new message from user {} in chat {}", sender.getUserId(), request.getChatId());

        // In a real app: save the message to the database and get a persistent ID.
        String serverMessageId = "msg-" + idGenerator.newId();
        Instant now = Instant.now();

        NewMessageEvent event = NewMessageEvent.newBuilder()
                .setMessageId(serverMessageId)
                .setChatId(request.getChatId())
                .setSenderId(sender.getUserId())
                .setContent(request.getContent())
                .setCreatedAt(TimestampConverter_CS1.fromInstant(now))
                .setClientMessageId(request.getClientMessageId()) // Echo back client ID
                .build();

        List<String> participants = getChatParticipants(request.getChatId());
        eventBroadcaster.broadcastNewMessage(participants, event);
        logExecutionTime(startTime);
    }

    /**
     * Creates a MessageStatusUpdateEvent and broadcasts it to chat participants.
     */
    @Override
    public void processStatusUpdate(UpdateStatusRequest request, AuthenticatedUser_CS1 updater) {
        long startTime = System.currentTimeMillis();
        logger.info("Processing status update for message {} by user {}", request.getMessageId(), updater.getUserId());

        // In a real app: validate the user can update this message (e.g., they are the recipient)
        // and then update the message status in the database.

        Instant now = Instant.now();

        MessageStatusUpdateEvent event = MessageStatusUpdateEvent.newBuilder()
                .setMessageId(request.getMessageId())
                .setChatId(request.getChatId())
                .setStatus(request.getStatus())
                .setUpdatedAt(TimestampConverter_CS1.fromInstant(now))
                .setUpdatedByUserId(updater.getUserId())
                .build();

        List<String> participants = getChatParticipants(request.getChatId());
        eventBroadcaster.broadcastStatusUpdate(participants, event);
        logExecutionTime(startTime);
    }

    /**
     * Retrieves participants for a given chat from the mock data store.
     */
    @Override
    public List<String> getChatParticipants(String chatId) {
        return chatMemberships.getOrDefault(chatId, Collections.emptyList());
    }

    private void logExecutionTime(long startTime) {
        long endTime = System.currentTimeMillis();
        logger.debug("Execution time: {} ms", (endTime - startTime));
    }
}
```
src/main/java/com/example/realtimemessaging/handler/ClientMessageHandler_CS1.java
<ctrl60><ctrl62>

    /**
     * Handles an incoming message from a client stream.
     * @param message The wrapper message from the client.
     * @param responseObserver The stream observer to send back responses or errors.
     */
    public void handleMessage(ClientToServerMessage message, StreamObserver<ServerToClientMessage> responseObserver) {
        long startTime = System.currentTimeMillis();
        AuthenticatedUser_CS1 user = GrpcConstants_CS1.AUTHENTICATED_USER_CONTEXT_KEY.get();
        if (user == null) {
            // This should theoretically not be reached due to the interceptor.
            responseObserver.onError(Status.UNAUTHENTICATED.withDescription("User is not authenticated.").asRuntimeException());
            return;
        }
        
        try {
            switch (message.getPayloadCase()) {
                case SEND_MESSAGE:
                    handleSendMessage(message.getSendMessage(), user);
                    break;
                case UPDATE_STATUS:
                    handleUpdateStatus(message.getUpdateStatus(), user);
                    break;
                case PAYLOAD_NOT_SET:
                    throw new GrpcStreamException_CS1(Status.INVALID_ARGUMENT, "Client message payload not set.");
                default:
                    throw new GrpcStreamException_CS1(Status.UNIMPLEMENTED, "Unknown payload type.");
            }
        } catch (GrpcStreamException_CS1 e) {
            // For validation errors, send a non-fatal StreamErrorEvent.
            logger.warn("Stream validation error for user {}: {}", user.getUserId(), e.getMessage());
            StreamErrorEvent errorEvent = buildStreamError(e);
            responseObserver.onNext(ServerToClientMessage.newBuilder().setStreamErrorEvent(errorEvent).build());
        } catch (Exception e) {
            // For unexpected errors, terminate the stream.
            logger.error("Unexpected error handling client message for user {}:", user.getUserId(), e);
            responseObserver.onError(Status.INTERNAL.withDescription("An internal server error occurred.").asRuntimeException());
        }
        logExecutionTime(startTime);
    }

    /**
     * Validates and processes a SendMessageRequest.
     */
    private void handleSendMessage(SendMessageRequest request, AuthenticatedUser_CS1 sender) {
        validationService.validateSendMessageRequest(request);
        validationService.validateUserIsMemberOfChat(sender.getUserId(), request.getChatId());
        messageService.processNewMessage(request, sender);
    }

    /**
     * Validates and processes an UpdateStatusRequest.
     */
    private void handleUpdateStatus(UpdateStatusRequest request, AuthenticatedUser_CS1 updater) {
        validationService.validateUpdateStatusRequest(request, updater.getUserId());
        validationService.validateUserIsMemberOfChat(updater.getUserId(), request.getChatId());
        messageService.processStatusUpdate(request, updater);
    }

    private StreamErrorEvent buildStreamError(GrpcStreamException_CS1 e) {
        StreamErrorEvent.Builder builder = StreamErrorEvent.newBuilder()
                .setErrorCode(StreamErrorCode_CS1.MALFORMED_REQUEST.name())
                .setErrorMessage(e.getStatus().getDescription());
        return builder.build();
    }

    private void logExecutionTime(long startTime) {
        long endTime = System.currentTimeMillis();
        logger.debug("Execution time: {} ms", (endTime - startTime));
    }
}
```
src/main/java/com/example/realtimemessaging/grpc/AuthTokenInterceptor_CS1.java
```java