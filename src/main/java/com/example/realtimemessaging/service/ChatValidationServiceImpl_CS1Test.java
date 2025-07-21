package com.example.realtimemessaging.service;

import com.example.realtimemessaging.exception.GrpcStreamException_CS1;
import com.example.realtimemessaging.grpc.MessageStatus;
import com.example.realtimemessaging.grpc.SendMessageRequest;
import com.example.realtimemessaging.grpc.UpdateStatusRequest;
import io.grpc.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ChatValidationServiceImpl_CS1Test {

    private ChatValidationServiceImpl_CS1 validationService;

    @BeforeEach
    void setUp() {
        validationService = new ChatValidationServiceImpl_CS1();
    }

    // validateUserIsMemberOfChat Tests
    @Test
    void validateUserIsMemberOfChat_withValidUserAndChat_shouldSucceed() {
        assertDoesNotThrow(() -> validationService.validateUserIsMemberOfChat("user-1", "chat-123"));
    }

    @Test
    void validateUserIsMemberOfChat_withInvalidUser_shouldThrowPermissionDenied() {
        GrpcStreamException_CS1 ex = assertThrows(GrpcStreamException_CS1.class,
                () -> validationService.validateUserIsMemberOfChat("user-999", "chat-123"));
        assertEquals(Status.Code.PERMISSION_DENIED, ex.getStatus().getCode());
    }

    @Test
    void validateUserIsMemberOfChat_withInvalidChat_shouldThrowNotFound() {
        GrpcStreamException_CS1 ex = assertThrows(GrpcStreamException_CS1.class,
                () -> validationService.validateUserIsMemberOfChat("user-1", "chat-999"));
        assertEquals(Status.Code.NOT_FOUND, ex.getStatus().getCode());
    }

    // validateSendMessageRequest Tests
    @Test
    void validateSendMessageRequest_withValidRequest_shouldSucceed() {
        SendMessageRequest request = SendMessageRequest.newBuilder()
                .setChatId("chat-123")
                .setClientMessageId("client-msg-id-1")
                .setContent("Hello World")
                .build();
        assertDoesNotThrow(() -> validationService.validateSendMessageRequest(request));
    }

    @Test
    void validateSendMessageRequest_withMissingChatId_shouldThrowInvalidArgument() {
        SendMessageRequest request = SendMessageRequest.newBuilder()
                .setClientMessageId("client-msg-id-1")
                .setContent("Hello World")
                .build();
        GrpcStreamException_CS1 ex = assertThrows(GrpcStreamException_CS1.class,
                () -> validationService.validateSendMessageRequest(request));
        assertEquals(Status.Code.INVALID_ARGUMENT, ex.getStatus().getCode());
        assertTrue(ex.getMessage().contains("chatId is required"));
    }

    // validateUpdateStatusRequest Tests
    @Test
    void validateUpdateStatusRequest_withValidSeenStatus_shouldSucceed() {
        UpdateStatusRequest request = UpdateStatusRequest.newBuilder()
                .setChatId("chat-123")
                .setMessageId("msg-abc")
                .setStatus(MessageStatus.SEEN)
                .build();
        assertDoesNotThrow(() -> validationService.validateUpdateStatusRequest(request, "user-2"));
    }

    @Test
    void validateUpdateStatusRequest_withNonSeenStatus_shouldThrowInvalidArgument() {
        UpdateStatusRequest request = UpdateStatusRequest.newBuilder()
                .setChatId("chat-123")
                .setMessageId("msg-abc")
                .setStatus(MessageStatus.DELIVERED) // Client cannot set this
                .build();
        GrpcStreamException_CS1 ex = assertThrows(GrpcStreamException_CS1.class,
                () -> validationService.validateUpdateStatusRequest(request, "user-2"));
        assertEquals(Status.Code.INVALID_ARGUMENT, ex.getStatus().getCode());
        assertTrue(ex.getMessage().contains("Client can only update status to SEEN"));
    }

    @Test
    void validateUpdateStatusRequest_withUnspecifiedStatus_shouldThrowInvalidArgument() {
        UpdateStatusRequest request = UpdateStatusRequest.newBuilder()
                .setChatId("chat-123")
                .setMessageId("msg-abc")
                .setStatus(MessageStatus.STATUS_UNSPECIFIED)
                .build();
        GrpcStreamException_CS1 ex = assertThrows(GrpcStreamException_CS1.class,
                () -> validationService.validateUpdateStatusRequest(request, "user-2"));
        assertEquals(Status.Code.INVALID_ARGUMENT, ex.getStatus().getCode());
        assertTrue(ex.getMessage().contains("status cannot be UNSPECIFIED"));
    }
}
```
src/test/java/com/example/realtimemessaging/service/MessageServiceImpl_CS1Test.java
```java