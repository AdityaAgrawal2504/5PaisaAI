package com.omqs.service;

import com.omqs.dto.EnqueueResponseDto_OMQS1;
import com.omqs.dto.MessageDto_OMQS1;
import com.omqs.dto.MessageRequestDto_OMQS1;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Interface defining the business logic for the Offline Message Queueing Service.
 */
public interface OfflineMessageService_OMQS1 {

    /**
     * Persists a single message for an offline user.
     */
    EnqueueResponseDto_OMQS1 enqueueMessage(UUID userId, MessageRequestDto_OMQS1 messageRequest);

    /**
     * Retrieves all queued messages for a user, up to a specified limit.
     */
    Map<UUID, MessageDto_OMQS1> dequeueMessages(UUID userId, int limit);

    /**
     * Acknowledges receipt of messages, removing them from the queue.
     */
    void acknowledgeMessages(UUID userId, Set<UUID> messageIds);

    /**
     * Periodically purges messages older than 7 days from the queue.
     */
    void purgeOldMessages();
}
```
```java
// src/main/java/com/omqs/service/OfflineMessageServiceImpl_OMQS1.java