package com.chatapp.api.fmhapi_v1.model.entity;

import com.chatapp.api.fmhapi_v1.model.enums.MessageContentTypeFMHAPI_V1;
import com.chatapp.api.fmhapi_v1.model.enums.MessageStatusFMHAPI_V1;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * JPA entity representing a message.
 */
@Entity
@Table(name = "messages")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageEntityFMHAPI_V1 {
    @Id
    private String id; // e.g., CUID for better cursor performance

    @Column(nullable = false)
    private UUID conversationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private UserEntityFMHAPI_V1 author;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessageContentTypeFMHAPI_V1 contentType;

    @Lob
    private String textContent;

    private String mediaUrl;

    private String fileName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessageStatusFMHAPI_V1 status;

    @Column(nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(nullable = false)
    private OffsetDateTime updatedAt;
}

