package com.example.fetchconversationsapi_v1.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "users_fca1")
public class UserEntity_FCA1 extends BaseEntity_FCA1 {
    @Column(nullable = false, unique = true)
    private String username;
    
    @Column(nullable = false)
    private String displayName;
    
    private String avatarUrl;

    @ManyToMany(mappedBy = "participants")
    private Set<ConversationEntity_FCA1> conversations;
}
```
```java
// JPA Entity for Message
// File: MessageEntity_FCA1.java