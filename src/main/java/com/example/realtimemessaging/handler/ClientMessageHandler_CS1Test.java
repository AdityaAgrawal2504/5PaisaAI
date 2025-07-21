package com.example.realtimemessaging.handler;

import com.example.realtimemessaging.constants.GrpcConstants_CS1;
import com.example.realtimemessaging.domain.AuthenticatedUser_CS1;
import com.example.realtimemessaging.exception.GrpcStreamException_CS1;
import com.example.realtimemessaging.grpc.*;
import com.example.realtimemessaging.service.ChatValidationService_CS1;
import com.example.realtimemessaging.service.MessageService_CS1;
import io.grpc.Context;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientMessageHandler_CS1Test {

    @Mock
    private ChatValidationService_CS1 mockValidationService;
    @Mock
    private MessageService_CS1 mockMessageService;
    @Mock
    private StreamObserver<ServerToClientMessage> mockResponseObserver;

    @InjectMocks
    private ClientMessageHandler_CS1 clientMessageHandler;

    private AuthenticatedUser_CS1 testUser;
    private Context.CancellableContext context;

    @BeforeEach
    void setUp() {
        testUser = new AuthenticatedUser_CS1("user-1", "testuser");
        // Set up the gRPC context for the test thread
        context = Context.current().withValue(GrpcConstants_CS1.AUTHENTICATED_USER_CONTEXT_KEY, testUser).attach();
    }

    @AfterEach
    void tearDown() {
        // Detach the context to avoid side effects
        Context.current().detach(context);
    }

    @Test
    void handleMessage_withSendMessageRequest_shouldValidateAndProcess() {
        SendMessageRequest sendMessageRequest = SendMessageRequest.newBuilder().setChatId("chat-123").setContent("Hi").build();
        ClientToServerMessage message = ClientToServerMessage.newBuilder().setSendMessage(sendMessageRequest).build();

        clientMessageHandler.handleMessage(message, mockResponseObserver);

        // Verify validation was called
        verify(mockValidationService).validateSendMessageRequest(sendMessageRequest);
        verify(mockValidationService).validateUserIsMemberOfChat(testUser.getUserId(), sendMessageRequest.getChatId());

        // Verify business logic was called
        verify(mockMessageService).processNewMessage(sendMessageRequest, testUser);
        verify(mockResponseObserver, never()).onError(any());
        verify(mockResponseObserver, never()).onNext(any());
    }

    @Test
    void handleMessage_withUpdateStatusRequest_shouldValidateAndProcess() {
        UpdateStatusRequest updateStatusRequest = UpdateStatusRequest.newBuilder().setMessageId("msg-1").setStatus(MessageStatus.SEEN).build();
        ClientToServerMessage message = ClientToServerMessage.newBuilder().setUpdateStatus(updateStatusRequest).build();

        clientMessageHandler.handleMessage(message, mockResponseObserver);

        verify(mockValidationService).validateUpdateStatusRequest(updateStatusRequest, testUser.getUserId());
        verify(mockMessageService).processStatusUpdate(updateStatusRequest, testUser);
        verify(mockResponseObserver, never()).onError(any());
    }

    @Test
    void handleMessage_whenValidationFails_shouldSendStreamErrorEvent() {
        SendMessageRequest sendMessageRequest = SendMessageRequest.newBuilder().setChatId("chat-123").setContent("").build(); // Invalid
        ClientToServerMessage message = ClientToServerMessage.newBuilder().setSendMessage(sendMessageRequest).build();

        // Mock validation service to throw our specific exception
        GrpcStreamException_CS1 validationException = new GrpcStreamException_CS1(Status.INVALID_ARGUMENT, "Content cannot be empty");
        doThrow(validationException).when(mockValidationService).validateSendMessageRequest(sendMessageRequest);

        clientMessageHandler.handleMessage(message, mockResponseObserver);

        ArgumentCaptor<ServerToClientMessage> captor = ArgumentCaptor.forClass(ServerToClientMessage.class);
        verify(mockResponseObserver).onNext(captor.capture());
        verify(mockResponseObserver, never()).onError(any());
        verify(mockMessageService, never()).processNewMessage(any(), any());

        ServerToClientMessage sentMessage = captor.getValue();
        assertTrue(sentMessage.hasStreamErrorEvent());
        assertEquals("Content cannot be empty", sentMessage.getStreamErrorEvent().getErrorMessage());
    }
    
    @Test
    void handleMessage_withPayloadNotSet_shouldThrowError() {
        ClientToServerMessage message = ClientToServerMessage.newBuilder().build(); // Empty payload

        clientMessageHandler.handleMessage(message, mockResponseObserver);
        
        ArgumentCaptor<ServerToClientMessage> captor = ArgumentCaptor.forClass(ServerToClientMessage.class);
        verify(mockResponseObserver).onNext(captor.capture());
        
        ServerToClientMessage sentMessage = captor.getValue();
        assertTrue(sentMessage.hasStreamErrorEvent());
        assertEquals("Client message payload not set.", sentMessage.getStreamErrorEvent().getErrorMessage());
    }
}
```
src/test/java/com/example/realtimemessaging/grpc/AuthTokenInterceptor_CS1Test.java
```java