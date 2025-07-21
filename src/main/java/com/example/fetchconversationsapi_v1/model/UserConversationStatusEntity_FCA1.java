package com.example.fetchconversationsapi_v1.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "user_conversation_status_fca1")
public class UserConversationStatusEntity_FCA1 extends BaseEntity_FCA1 {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity_FCA1 user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversation_id", nullable = false)
    private ConversationEntity_FCA1 conversation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "last_seen_message_id")
    private MessageEntity_FCA1 lastSeenMessage;

    @Column(nullable = false)
    private int unreadCount = 0;
}
```
```java
// JPA Repository for Conversations
// File: ConversationRepository_FCA1.java