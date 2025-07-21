package com.example.fetchconversationsapi_v1.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "messages_fca1")
public class MessageEntity_FCA1 extends BaseEntity_FCA1 {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversation_id", nullable = false)
    private ConversationEntity_FCA1 conversation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private UserEntity_FCA1 sender;

    @Column(columnDefinition = "TEXT")
    private String content;
}
```
```java
// JPA Entity for Conversation
// File: ConversationEntity_FCA1.java