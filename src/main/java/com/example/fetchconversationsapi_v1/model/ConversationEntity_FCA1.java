package com.example.fetchconversationsapi_v1.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "conversations_fca1")
public class ConversationEntity_FCA1 extends BaseEntity_FCA1 {
    private String title; // Optional, can be null for 1-on-1 chats

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "conversation_participants_fca1",
        joinColumns = @JoinColumn(name = "conversation_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<UserEntity_FCA1> participants = new HashSet<>();

    @OneToMany(mappedBy = "conversation", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("createdAt DESC")
    private Set<MessageEntity_FCA1> messages = new HashSet<>();

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "last_message_id")
    private MessageEntity_FCA1 lastMessage;

    @OneToMany(mappedBy = "conversation", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserConversationStatusEntity_FCA1> userStatuses = new HashSet<>();
}
```
```java
// JPA Entity for User Conversation Status (seen, unread count)
// File: UserConversationStatusEntity_FCA1.java