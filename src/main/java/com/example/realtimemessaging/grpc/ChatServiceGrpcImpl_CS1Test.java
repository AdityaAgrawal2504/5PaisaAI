package com.example.realtimemessaging.grpc;

import com.example.realtimemessaging.constants.GrpcConstants_CS1;
import com.example.realtimemessaging.domain.AuthenticatedUser_CS1;
import com.example.realtimemessaging.handler.ClientMessageHandler_CS1;
import com.example.realtimemessaging.handler.StreamLifecycleHandler_CS1;
import com.example.realtimemessaging.manager.SessionManager_CS1;
import com.example.realtimemessaging.util.IdGenerator_CS1;
import io.grpc.Context;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

// Using MockitoExtension as SpringExtension is not strictly needed for this unit test
@ExtendWith(MockitoExtension.class)
class ChatServiceGrpcImpl_CS1Test {

    @Mock
    private ClientMessageHandler_CS1 mockClientMessageHandler;
    @Mock
    private SessionManager_CS1 mockSessionManager;
    @Mock
    private IdGenerator_CS1 mockIdGenerator;
    @Mock
    private StreamObserver<ServerToClientMessage> mockResponseObserver;
    @InjectMocks
    private ChatServiceGrpcImpl_CS1 chatService;

    private AuthenticatedUser_CS1 testUser;
    private Context.CancellableContext context;
    
    @BeforeEach
    void setUp() {
        testUser = new AuthenticatedUser_CS1("user-1", "testuser");
        context = Context.current().withValue(GrpcConstants_CS1.AUTHENTICATED_USER_CONTEXT_KEY, testUser).attach();
        when(mockIdGenerator.newId()).thenReturn("test-id");
    }

    @AfterEach
    void tearDown() {
        Context.current().detach(context);
    }

    @Test
    void connectStream_withAuthenticatedUser_shouldEstablishStream() {
        // Act
        StreamObserver<ClientToServerMessage> requestObserver = chatService.connectStream(mockResponseObserver);

        // Assert
        assertNotNull(requestObserver);
        // Verify welcome event was sent
        verify(mockResponseObserver, times(1)).onNext(argThat(msg -> msg.hasWelcomeEvent()));
        // Verify session was registered
        verify(mockSessionManager, times(1)).registerSession(eq("session-test-id"), eq("user-1"), any());
    }

    @Test
    void connectStream_withNoUserInContext_shouldReturnError() {
        // Arrange
        Context.current().detach(context); // Remove user from context

        // Act
        StreamObserver<ClientToServerMessage> requestObserver = chatService.connectStream(mockResponseObserver);

        // Assert
        assertNotNull(requestObserver);
        verify(mockResponseObserver).onError(any(io.grpc.StatusRuntimeException.class));
        verify(mockSessionManager, never()).registerSession(any(), any(), any());
    }

    @Test
    void streamObserver_onNext_shouldDelegateToMessageHandler() {
        // Arrange
        StreamObserver<ClientToServerMessage> requestObserver = chatService.connectStream(mockResponseObserver);
        ClientToServerMessage message = ClientToServerMessage.newBuilder().setSendMessage(SendMessageRequest.getDefaultInstance()).build();
        
        // Act
        requestObserver.onNext(message);

        // Assert
        verify(mockClientMessageHandler).handleMessage(eq(message), eq(mockResponseObserver));
    }
    
    @Test
    void streamObserver_onCompleted_shouldUnregisterSession() {
        // Arrange
        StreamObserver<ClientToServerMessage> requestObserver = chatService.connectStream(mockResponseObserver);
        
        // Act
        requestObserver.onCompleted();

        // Assert
        verify(mockSessionManager).unregisterSession("session-test-id");
        verify(mockResponseObserver).onCompleted();
    }
    
    @Test
    void streamObserver_onError_shouldUnregisterSession() {
        // Arrange
        StreamObserver<ClientToServerMessage> requestObserver = chatService.connectStream(mockResponseObserver);
        Throwable error = new RuntimeException("Client disconnected abruptly");
        
        // Act
        requestObserver.onError(error);
        
        // Assert
        verify(mockSessionManager).unregisterSession("session-test-id");
        verify(mockResponseObserver, never()).onError(any()); // The service impl shouldn't propagate the client-side error back
    }
}
```