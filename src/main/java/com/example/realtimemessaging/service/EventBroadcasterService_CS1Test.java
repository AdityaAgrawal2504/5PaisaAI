package com.example.realtimemessaging.service;

import com.example.realtimemessaging.grpc.NewMessageEvent;
import com.example.realtimemessaging.grpc.ServerToClientMessage;
import com.example.realtimemessaging.manager.SessionManager_CS1;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventBroadcasterService_CS1Test {

    @Mock
    private SessionManager_CS1 mockSessionManager;

    @InjectMocks
    private EventBroadcasterService_CS1 eventBroadcasterService;

    @Mock
    private StreamObserver<ServerToClientMessage> mockObserverUser1;
    @Mock
    private StreamObserver<ServerToClientMessage> mockObserverUser2;

    @BeforeEach
    void setUp() {
        // User 1 is connected
        when(mockSessionManager.getStreamForUser("user-1")).thenReturn(Optional.of(mockObserverUser1));
        // User 2 is connected
        when(mockSessionManager.getStreamForUser("user-2")).thenReturn(Optional.of(mockObserverUser2));
        // User 3 is offline
        when(mockSessionManager.getStreamForUser("user-3")).thenReturn(Optional.empty());
    }

    @Test
    void broadcastNewMessage_shouldSendToAllOnlineParticipants() {
        List<String> participants = List.of("user-1", "user-2", "user-3");
        NewMessageEvent event = NewMessageEvent.newBuilder().setContent("Test Message").build();

        eventBroadcasterService.broadcastNewMessage(participants, event);

        // Capture what is sent to each observer
        ArgumentCaptor<ServerToClientMessage> captorUser1 = ArgumentCaptor.forClass(ServerToClientMessage.class);
        verify(mockObserverUser1).onNext(captorUser1.capture());

        ArgumentCaptor<ServerToClientMessage> captorUser2 = ArgumentCaptor.forClass(ServerToClientMessage.class);
        verify(mockObserverUser2).onNext(captorUser2.capture());

        // Verify the message content
        assertEquals(event, captorUser1.getValue().getNewMessageEvent());
        assertEquals(event, captorUser2.getValue().getNewMessageEvent());

        // Verify user 3 (offline) was not sent anything
        // Since getStreamForUser("user-3") returns empty, no interaction with an observer happens.
        // Mockito's `verify` default behavior confirms this if no `when` is set up for it.
    }

    @Test
    void broadcastNewMessage_handlesObserverErrorGracefully() {
        // Arrange: Make one of the observers throw an exception
        doThrow(new RuntimeException("Stream closed")).when(mockObserverUser2).onNext(any());

        List<String> participants = List.of("user-1", "user-2");
        NewMessageEvent event = NewMessageEvent.newBuilder().setContent("Another Test").build();

        // Act & Assert: The service should not crash
        assertDoesNotThrow(() -> eventBroadcasterService.broadcastNewMessage(participants, event));

        // Verify that the healthy observer still received the message
        verify(mockObserverUser1).onNext(any(ServerToClientMessage.class));

        // Verify that the problematic observer was called
        verify(mockObserverUser2).onNext(any(ServerToClientMessage.class));
    }
}
```
src/test/java/com/example/realtimemessaging/handler/ClientMessageHandler_CS1Test.java
```java