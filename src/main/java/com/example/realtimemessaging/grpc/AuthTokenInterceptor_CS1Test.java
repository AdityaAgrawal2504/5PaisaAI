package com.example.realtimemessaging.grpc;

import com.example.realtimemessaging.constants.GrpcConstants_CS1;
import com.example.realtimemessaging.domain.AuthenticatedUser_CS1;
import com.example.realtimemessaging.service.AuthenticationService_CS1;
import io.grpc.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthTokenInterceptor_CS1Test {

    @Mock
    private AuthenticationService_CS1 mockAuthService;
    @Mock
    private ServerCall<String, Integer> mockServerCall;
    @Mock
    private ServerCallHandler<String, Integer> mockNext;
    @Mock
    private ServerCall.Listener<String> mockListener;

    @Captor
    private ArgumentCaptor<Status> statusCaptor;
    @Captor
    private ArgumentCaptor<Context> contextCaptor;

    private AuthTokenInterceptor_CS1 interceptor;

    @BeforeEach
    void setUp() {
        interceptor = new AuthTokenInterceptor_CS1(mockAuthService);
    }

    @Test
    void interceptCall_withValidToken_shouldAuthenticateAndProceed() {
        // Arrange
        String token = "Bearer valid.token";
        Metadata headers = new Metadata();
        headers.put(GrpcConstants_CS1.AUTHORIZATION_METADATA_KEY, token);

        AuthenticatedUser_CS1 authUser = new AuthenticatedUser_CS1("user-1", "testuser");
        when(mockAuthService.authenticate(token)).thenReturn(Optional.of(authUser));
        when(mockNext.startCall(any(), any())).thenReturn(mockListener);

        // Act
        ServerCall.Listener<String> resultListener = interceptor.interceptCall(mockServerCall, headers, mockNext);

        // Assert
        verify(mockAuthService).authenticate(token);
        // Verify that the call proceeds with an updated context
        verify(Contexts.class, times(1));
        Contexts.interceptCall(contextCaptor.capture(), eq(mockServerCall), eq(headers), eq(mockNext));

        assertNotNull(resultListener);
        assertNotEquals(mockServerCall, resultListener); // Should be a new listener
        assertEquals(authUser, GrpcConstants_CS1.AUTHENTICATED_USER_CONTEXT_KEY.get(contextCaptor.getValue()));
        verify(mockServerCall, never()).close(any(), any());
    }

    @Test
    void interceptCall_withInvalidToken_shouldCloseCallWithUnauthenticated() {
        // Arrange
        String token = "Bearer invalid.token";
        Metadata headers = new Metadata();
        headers.put(GrpcConstants_CS1.AUTHORIZATION_METADATA_KEY, token);
        when(mockAuthService.authenticate(token)).thenReturn(Optional.empty());

        // Act
        ServerCall.Listener<String> resultListener = interceptor.interceptCall(mockServerCall, headers, mockNext);

        // Assert
        verify(mockAuthService).authenticate(token);
        verify(mockNext, never()).startCall(any(), any());
        verify(mockServerCall).close(statusCaptor.capture(), any(Metadata.class));
        assertNotNull(resultListener);
        assertEquals(Status.Code.UNAUTHENTICATED, statusCaptor.getValue().getCode());
    }

    @Test
    void interceptCall_withMissingToken_shouldCloseCallWithUnauthenticated() {
        // Arrange
        Metadata headers = new Metadata(); // No token
        when(mockAuthService.authenticate(null)).thenReturn(Optional.empty());

        // Act
        ServerCall.Listener<String> resultListener = interceptor.interceptCall(mockServerCall, headers, mockNext);

        // Assert
        verify(mockAuthService).authenticate(null);
        verify(mockNext, never()).startCall(any(), any());
        verify(mockServerCall).close(statusCaptor.capture(), any(Metadata.class));
        assertNotNull(resultListener);
        assertEquals(Status.Code.UNAUTHENTICATED, statusCaptor.getValue().getCode());
    }
}
```
src/test/java/com/example/realtimemessaging/grpc/ChatServiceGrpcImpl_CS1Test.java
```java