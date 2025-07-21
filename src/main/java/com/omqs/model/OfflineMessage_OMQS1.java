package com.omqs.model;

import com.omqs.util.PayloadConverter_OMQS1;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * JPA entity representing a queued message in the database.
 */
@Entity
@Table(name = "offline_messages", indexes = {
    @Index(name = "idx_recipient_id", columnList = "recipientId"),
    @Index(name = "idx_enqueued_at", columnList = "enqueuedAt")
})
@Data
@NoArgsConstructor
public class OfflineMessage_OMQS1 {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(updatable = false, nullable = false)
    private UUID messageId;

    @Column(nullable = false)
    private UUID senderId;

    @Column(nullable = false)
    private UUID recipientId;

    @Column(nullable = false)
    private Instant timestamp;

    @Column(nullable = false)
    private Instant enqueuedAt;

    @Column(columnDefinition = "TEXT", nullable = false)
    @Convert(converter = PayloadConverter_OMQS1.class)
    private Map<String, Object> payload;
}
```
```java
// src/main/java/com/omqs/repository/OfflineMessageRepository_OMQS1.java