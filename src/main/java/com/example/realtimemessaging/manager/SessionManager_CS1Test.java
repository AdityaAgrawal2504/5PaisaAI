package com.example.realtimemessaging.manager;

import com.example.realtimemessaging.grpc.ServerToClientMessage;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SessionManager_CS1Test {

    private SessionManager_CS1 sessionManager;

    @Mock
    private StreamObserver<ServerToClientMessage> mockObserver1;
    @Mock
    private StreamObserver<ServerToClientMessage> mockObserver2;

    @BeforeEach
    void setUp() {
        sessionManager = new SessionManager_CS1();
    }

    @Test
    void registerSession_shouldAddNewSessionSuccessfully() {
        String sessionId = "session-1";
        String userId = "user-1";

        sessionManager.registerSession(sessionId, userId, mockObserver1);

        Optional<StreamObserver<ServerToClientMessage>> retrievedObserver = sessionManager.getStreamForUser(userId);
        assertTrue(retrievedObserver.isPresent());
        assertEquals(mockObserver1, retrievedObserver.get());
    }

    @Test
    void registerSession_withExistingUser_shouldTerminateOldSession() {
        String oldSessionId = "session-old";
        String newSessionId = "session-new";
        String userId = "user-1";

        // Register the first session
        sessionManager.registerSession(oldSessionId, userId, mockObserver1);
        Optional<StreamObserver<ServerToClientMessage>> retrievedObserver1 = sessionManager.getStreamForUser(userId);
        assertTrue(retrievedObserver1.isPresent());

        // Register a new session for the same user
        sessionManager.registerSession(newSessionId, userId, mockObserver2);

        // Verify the old observer was terminated
        verify(mockObserver1).onError(any(io.grpc.StatusRuntimeException.class));

        // Verify the new session is now active
        Optional<StreamObserver<ServerToClientMessage>> retrievedObserver2 = sessionManager.getStreamForUser(userId);
        assertTrue(retrievedObserver2.isPresent());
        assertEquals(mockObserver2, retrievedObserver2.get());
    }

    @Test
    void unregisterSession_shouldRemoveSessionAndUserMapping() {
        String sessionId = "session-1";
        String userId = "user-1";

        sessionManager.registerSession(sessionId, userId, mockObserver1);
        assertTrue(sessionManager.getStreamForUser(userId).isPresent());

        sessionManager.unregisterSession(sessionId);

        assertFalse(sessionManager.getStreamForUser(userId).isPresent());
    }

    @Test
    void getStreamForUser_withNonExistentUser_shouldReturnEmpty() {
        Optional<StreamObserver<ServerToClientMessage>> result = sessionManager.getStreamForUser("non-existent-user");
        assertFalse(result.isPresent());
    }
}
```
src/test/java/com/example/realtimemessaging/service/ChatValidationServiceImpl_CS1Test.java
```java