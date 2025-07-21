package com.example.realtimemessaging.service;

import com.example.realtimemessaging.domain.AuthenticatedUser_CS1;
import com.example.realtimemessaging.grpc.*;
import com.example.realtimemessaging.util.IdGenerator_CS1;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageServiceImpl_CS1Test {

    @Mock
    private EventBroadcasterService_CS1 mockEventBroadcaster;
    @Mock
    private IdGenerator_CS1 mockIdGenerator;

    @InjectMocks
    private MessageServiceImpl_CS1 messageService;

    private AuthenticatedUser_CS1 testUser;

    @BeforeEach
    void setUp() {
        testUser = new AuthenticatedUser_CS1("user-1", "testuser");
    }

    @Test
    void processNewMessage_shouldGenerateIdAndBroadcastEvent() {
        String generatedMsgId = "msg-generated-id";
        when(mockIdGenerator.newId()).thenReturn(generatedMsgId);

        SendMessageRequest request = SendMessageRequest.newBuilder()
                .setChatId("chat-123")
                .setClientMessageId("client-id-1")
                .setContent("Hello")
                .build();

        messageService.processNewMessage(request, testUser);

        ArgumentCaptor<List<String>> participantsCaptor = ArgumentCaptor.forClass(List.class);
        ArgumentCaptor<NewMessageEvent> eventCaptor = ArgumentCaptor.forClass(NewMessageEvent.class);

        verify(mockEventBroadcaster).broadcastNewMessage(participantsCaptor.capture(), eventCaptor.capture());

        // Verify participants are correct for chat-123
        assertEquals(List.of("user-1", "user-2"), participantsCaptor.getValue());

        // Verify the event content is correct
        NewMessageEvent capturedEvent = eventCaptor.getValue();
        assertEquals("msg-" + generatedMsgId, capturedEvent.getMessageId());
        assertEquals(request.getChatId(), capturedEvent.getChatId());
        assertEquals(testUser.getUserId(), capturedEvent.getSenderId());
        assertEquals(request.getContent(), capturedEvent.getContent());
        assertEquals(request.getClientMessageId(), capturedEvent.getClientMessageId());
    }

    @Test
    void processStatusUpdate_shouldBroadcastEvent() {
        UpdateStatusRequest request = UpdateStatusRequest.newBuilder()
                .setChatId("chat-123")
                .setMessageId("msg-to-update")
                .setStatus(MessageStatus.SEEN)
                .build();

        messageService.processStatusUpdate(request, testUser);

        ArgumentCaptor<List<String>> participantsCaptor = ArgumentCaptor.forClass(List.class);
        ArgumentCaptor<MessageStatusUpdateEvent> eventCaptor = ArgumentCaptor.forClass(MessageStatusUpdateEvent.class);

        verify(mockEventBroadcaster).broadcastStatusUpdate(participantsCaptor.capture(), eventCaptor.capture());

        assertEquals(List.of("user-1", "user-2"), participantsCaptor.getValue());

        MessageStatusUpdateEvent capturedEvent = eventCaptor.getValue();
        assertEquals(request.getMessageId(), capturedEvent.getMessageId());
        assertEquals(request.getChatId(), capturedEvent.getChatId());
        assertEquals(request.getStatus(), capturedEvent.getStatus());
        assertEquals(testUser.getUserId(), capturedEvent.getUpdatedByUserId());
    }

    @Test
    void getChatParticipants_withValidChat_shouldReturnMembers() {
        List<String> participants = messageService.getChatParticipants("chat-456");
        assertEquals(List.of("user-1", "user-3"), participants);
    }

    @Test
    void getChatParticipants_withInvalidChat_shouldReturnEmptyList() {
        List<String> participants = messageService.getChatParticipants("chat-invalid");
        assertTrue(participants.isEmpty());
    }
}
```
src/test/java/com/example/realtimemessaging/service/EventBroadcasterService_CS1Test.java
```java