package com.omqs.repository;

import com.omqs.model.OfflineMessage_OMQS1;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Spring Data JPA repository for the OfflineMessage entity.
 */
@Repository
public interface OfflineMessageRepository_OMQS1 extends JpaRepository<OfflineMessage_OMQS1, UUID> {

    /**
     * Finds messages for a specific recipient, ordered by enqueue time.
     */
    List<OfflineMessage_OMQS1> findByRecipientIdOrderByEnqueuedAtAsc(UUID recipientId, Pageable pageable);

    /**
     * Counts messages for a specific recipient with given message IDs.
     */
    long countByRecipientIdAndMessageIdIn(UUID recipientId, Set<UUID> messageIds);

    /**
     * Deletes messages for a specific recipient by their IDs.
     */
    @Transactional
    @Modifying
    void deleteByRecipientIdAndMessageIdIn(UUID recipientId, Set<UUID> messageIds);

    /**
     * Deletes all messages that were enqueued before a specified cutoff time.
     */
    @Transactional
    @Modifying
    @Query("DELETE FROM OfflineMessage_OMQS1 m WHERE m.enqueuedAt < :cutoff")
    int deleteByEnqueuedAtBefore(Instant cutoff);
}
```
```java
// src/main/java/com/omqs/dto/MessageDto_OMQS1.java