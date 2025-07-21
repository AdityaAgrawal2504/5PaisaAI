package com.example.realtimemessaging.grpc;

import com.example.realtimemessaging.constants.GrpcConstants_CS1;
import com.example.realtimemessaging.domain.AuthenticatedUser_CS1;
import com.example.realtimemessaging.handler.ClientMessageHandler_CS1;
import com.example.realtimemessaging.handler.StreamLifecycleHandler_CS1;
import com.example.realtimemessaging.manager.SessionManager_CS1;
import com.example.realtimemessaging.util.IdGenerator_CS1;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The main gRPC service implementation for the ChatService.
 */
@GrpcService
public class ChatServiceGrpcImpl_CS1 extends ChatServiceGrpc.ChatServiceImplBase {

    private static final Logger logger = LoggerFactory.getLogger(ChatServiceGrpcImpl_CS1.class);

    private final ClientMessageHandler_CS1 clientMessageHandler;
    private final SessionManager_CS1 sessionManager;
    private final IdGenerator_CS1 idGenerator;

    public ChatServiceGrpcImpl_CS1(ClientMessageHandler_CS1 clientMessageHandler,
                                   SessionManager_CS1 sessionManager,
                                   IdGenerator_CS1 idGenerator) {
        this.clientMessageHandler = clientMessageHandler;
        this.sessionManager = sessionManager;
        this.idGenerator = idGenerator;
    }

    /**
     * Establishes the bi-directional stream for a connecting client.
     */
    @Override
    public StreamObserver<ClientToServerMessage> connectStream(StreamObserver<ServerToClientMessage> responseObserver) {
        long startTime = System.currentTimeMillis();
        AuthenticatedUser_CS1 user = GrpcConstants_CS1.AUTHENTICATED_USER_CONTEXT_KEY.get();
        if (user == null) {
            // This case should be caught by the AuthTokenInterceptor, but as a safeguard:
            responseObserver.onError(io.grpc.Status.UNAUTHENTICATED
                .withDescription("Authentication failed. User context not found.")
                .asRuntimeException());
            return new NoOpRequestObserver_CS1();
        }

        // Create a new lifecycle handler for this specific connection
        StreamLifecycleHandler_CS1 lifecycleHandler = new StreamLifecycleHandler_CS1(sessionManager, idGenerator);
        lifecycleHandler.onConnect(user, responseObserver);
        
        logExecutionTime(startTime);

        // Return a new StreamObserver to handle messages from this client
        return new StreamObserver<>() {
            @Override
            public void onNext(ClientToServerMessage message) {
                // Delegate message handling
                clientMessageHandler.handleMessage(message, responseObserver);
            }

            @Override
            public void onError(Throwable t) {
                // Delegate error handling
                lifecycleHandler.onError(t);
            }

            @Override
            public void onCompleted() {
                // Delegate completion handling
                lifecycleHandler.onCompleted();
                responseObserver.onCompleted();
            }
        };
    }

    private void logExecutionTime(long startTime) {
        long endTime = System.currentTimeMillis();
        logger.debug("connectStream setup time: {} ms", (endTime - startTime));
    }
    
    /**
     * A no-op StreamObserver for early termination cases.
     */
    private static class NoOpRequestObserver_CS1 implements StreamObserver<ClientToServerMessage> {
        @Override
        public void onNext(ClientToServerMessage value) {}
        @Override
        public void onError(Throwable t) {}
        @Override
        public void onCompleted() {}
    }
}
```
src/test/java/com/example/realtimemessaging/service/AuthenticationServiceImpl_CS1Test.java
```java