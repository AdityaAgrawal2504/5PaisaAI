package com.example.realtimemessaging.handler;

import com.example.realtimemessaging.domain.AuthenticatedUser_CS1;
import com.example.realtimemessaging.grpc.ServerToClientMessage;
import com.example.realtimemessaging.grpc.WelcomeEvent;
import com.example.realtimemessaging.manager.SessionManager_CS1;
import com.example.realtimemessaging.util.IdGenerator_CS1;
import com.example.realtimemessaging.util.TimestampConverter_CS1;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * Handles the lifecycle events of a client stream, such as connection, disconnection, and errors.
 */
@Component
public class StreamLifecycleHandler_CS1 {

    private static final Logger logger = LoggerFactory.getLogger(StreamLifecycleHandler_CS1.class);

    private final SessionManager_CS1 sessionManager;
    private final IdGenerator_CS1 idGenerator;
    private final String sessionId;

    // A handler is created for each new stream, holding stream-specific state.
    public StreamLifecycleHandler_CS1(SessionManager_CS1 sessionManager, IdGenerator_CS1 idGenerator) {
        this.sessionManager = sessionManager;
        this.idGenerator = idGenerator;
        this.sessionId = "session-" + idGenerator.newId();
    }

    /**
     * Handles the initial connection setup for a new client stream.
     * @param user The authenticated user establishing the connection.
     * @param responseObserver The stream to send messages back to the client.
     */
    public void onConnect(AuthenticatedUser_CS1 user, StreamObserver<ServerToClientMessage> responseObserver) {
        long startTime = System.currentTimeMillis();
        // Add session context to logs for this stream
        MDC.put("sessionId", sessionId);
        MDC.put("userId", user.getUserId());

        logger.info("Client connected. Establishing session.");
        sessionManager.registerSession(sessionId, user.getUserId(), responseObserver);

        WelcomeEvent welcomeEvent = WelcomeEvent.newBuilder()
                .setSessionId(sessionId)
                .setServerTimestamp(TimestampConverter_CS1.fromInstant(Instant.now()))
                .build();

        responseObserver.onNext(ServerToClientMessage.newBuilder().setWelcomeEvent(welcomeEvent).build());
        logExecutionTime(startTime);
    }

    /**
     * Handles the clean termination of a client stream.
     */
    public void onCompleted() {
        long startTime = System.currentTimeMillis();
        logger.info("Client stream completed.");
        sessionManager.unregisterSession(sessionId);
        MDC.clear(); // Clean up logging context
        logExecutionTime(startTime);
    }

    /**
     * Handles an error occurring in a client stream, leading to its termination.
     * @param t The throwable that caused the error.
     */
    public void onError(Throwable t) {
        long startTime = System.currentTimeMillis();
        logger.error("Client stream terminated with error: {}", t.getMessage(), t);
        sessionManager.unregisterSession(sessionId);
        MDC.clear(); // Clean up logging context
        logExecutionTime(startTime);
    }

    private void logExecutionTime(long startTime) {
        long endTime = System.currentTimeMillis();
        logger.debug("Execution time: {} ms", (endTime - startTime));
    }
}
```
src/main/java/com/example/realtimemessaging/grpc/ChatServiceGrpcImpl_CS1.java
```java