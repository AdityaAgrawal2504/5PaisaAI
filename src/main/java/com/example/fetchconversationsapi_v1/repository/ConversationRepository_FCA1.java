package com.example.fetchconversationsapi_v1.repository;

import com.example.fetchconversationsapi_v1.model.ConversationEntity_FCA1;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ConversationRepository_FCA1 extends JpaRepository<ConversationEntity_FCA1, UUID>, JpaSpecificationExecutor<ConversationEntity_FCA1> {
}
```
```java
// JPA Specification builder for dynamic queries
// File: ConversationSpecification_FCA1.java