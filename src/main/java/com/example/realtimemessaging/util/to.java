package com.example.realtimemessaging.util;

import com.google.protobuf.Timestamp;
import java.time.Instant;

/**
 * Utility class to convert between java.time.Instant and google.protobuf.Timestamp.
 */
public final class TimestampConverter_CS1 {

    private TimestampConverter_CS1() {}

    /**
     * Converts a java.time.Instant to a google.protobuf.Timestamp.
     * @param instant The Instant to convert.
     * @return The corresponding Timestamp.
     */
    public static Timestamp fromInstant(Instant instant) {
        if (instant == null) {
            return null;
        }
        return Timestamp.newBuilder()
                .setSeconds(instant.getEpochSecond())
                .setNanos(instant.getNano())
                .build();
    }

    /**
     * Converts a google.protobuf.Timestamp to a java.time.Instant.
     * @param timestamp The Timestamp to convert.
     * @return The corresponding Instant.
     */
    public static Instant toInstant(Timestamp timestamp) {
        if (timestamp == null) {
            return null;
        }
        return Instant.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos());
    }
}
```
src/main/java/com/example/realtimemessaging/manager/SessionManager_CS1.java
<ctrl60>package com.example.realtimemessaging.manager;

import com.example.realtimemessaging.grpc.ServerToClientMessage;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages active user sessions and their corresponding gRPC streams.
 * This class is thread-safe.
 */
@Component
public class SessionManager_CS1 {

    private static final Logger logger = LoggerFactory.getLogger(SessionManager_CS1.class);

    // Maps a unique session ID to the client's response stream observer.
    private final Map<String, StreamObserver<ServerToClientMessage>> activeSessions = new ConcurrentHashMap<>();

    // Maps a user ID to their active session ID. A user can have one active session.
    private final Map<String, String> userToSessionMap = new ConcurrentHashMap<>();

    /**
     * Registers a new client session. If a session for the user already exists, it is terminated.
     * @param sessionId A unique ID for this specific stream connection.
     * @param userId The ID of the user who owns the session.
     * @param responseObserver The gRPC stream observer for sending messages to the client.
     */
    public void registerSession(String sessionId, String userId, StreamObserver<ServerToClientMessage> responseObserver) {
        long startTime = System.currentTimeMillis();
        // If user already has an active session, terminate it with an error.
        String existingSessionId = userToSessionMap.get(userId);
        if (existingSessionId != null) {
            Optional.ofNullable(activeSessions.get(existingSessionId)).ifPresent(observer -> {
                logger.warn("User {} already has an active session {}. Terminating old session.", userId, existingSessionId);
                try {
                    observer.onError(io.grpc.Status.ALREADY_EXISTS
                        .withDescription("New connection established, this stream is being terminated.")
                        .asRuntimeException());
                } catch (Exception e) {
                   logger.error("Error terminating stale session {}", existingSessionId, e);
                }
            });
        }

        activeSessions.put(sessionId, responseObserver);
        userToSessionMap.put(userId, sessionId);
        logger.info("Session {} registered for user {}", sessionId, userId);
        logExecutionTime(startTime);
    }

    /**
     * Removes a session upon client disconnection.
     * @param sessionId The unique ID of the session to remove.
     */
    public void unregisterSession(String sessionId) {
        long startTime = System.currentTimeMillis();
        activeSessions.remove(sessionId);
        userToSessionMap.entrySet().removeIf(entry -> entry.getValue().equals(sessionId));
        logger.info("Session {} unregistered.", sessionId);
        logExecutionTime(startTime);
    }

    /**
     * Retrieves the response stream for a given user ID.
     * @param userId The ID of the user.
     * @return An Optional containing the StreamObserver if the user has an active session.
     */
    public Optional<StreamObserver<ServerToClientMessage>> getStreamForUser(String userId) {
        long startTime = System.currentTimeMillis();
        String sessionId = userToSessionMap.get(userId);
        Optional<StreamObserver<ServerToClientMessage>> result = Optional.ofNullable(sessionId)
                .map(activeSessions::get);
        logExecutionTime(startTime);
        return result;
    }

    private void logExecutionTime(long startTime) {
        long endTime = System.currentTimeMillis();
        logger.debug("Execution time: {} ms", (endTime - startTime));
    }
}
```
src/main/java/com/example/realtimemessaging/service/EventBroadcasterService_CS1.java
<ctrl60><ctrl62>
    }

    private void logExecutionTime(long startTime) {
        long endTime = System.currentTimeMillis();
        logger.debug("Execution time: {} ms", (endTime - startTime));
    }
}
```
src/main/java/com/example/realtimemessaging/service/ChatValidationService_CS1.java
```java