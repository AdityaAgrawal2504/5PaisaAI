package com.example.fetchconversationsapi_v1.repository;

import com.example.fetchconversationsapi_v1.model.*;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.UUID;

public class ConversationSpecification_FCA1 {

    /**
     * Creates a specification to find conversations for a specific user.
     */
    public static Specification<ConversationEntity_FCA1> hasParticipant(UUID userId) {
        return (root, query, cb) -> {
            Join<ConversationEntity_FCA1, UserEntity_FCA1> participants = root.join("participants");
            return cb.equal(participants.get("id"), userId);
        };
    }

    /**
     * Creates a specification to filter conversations based on seen status.
     */
    public static Specification<ConversationEntity_FCA1> isSeen(boolean seen, UUID userId) {
        return (root, query, cb) -> {
            Subquery<UUID> subquery = query.subquery(UUID.class);
            Root<UserConversationStatusEntity_FCA1> statusRoot = subquery.from(UserConversationStatusEntity_FCA1.class);
            subquery.select(statusRoot.get("conversation").get("id"));
            subquery.where(
                cb.and(
                    cb.equal(statusRoot.get("user").get("id"), userId),
                    cb.isNotNull(root.get("lastMessage")),
                    cb.isNotNull(statusRoot.get("lastSeenMessage")),
                    seen ? cb.greaterThanOrEqualTo(statusRoot.get("lastSeenMessage").get("id"), root.get("lastMessage").get("id"))
                         : cb.lessThan(statusRoot.get("lastSeenMessage").get("id"), root.get("lastMessage").get("id"))
                )
            );
            return root.get("id").in(subquery);
        };
    }
    
    /**
     * Creates a specification for text search in participant names and message content.
     */
    public static Specification<ConversationEntity_FCA1> hasSearchQuery(String searchQuery) {
        if (!StringUtils.hasText(searchQuery)) {
            return null;
        }
        String pattern = "%" + searchQuery.toLowerCase() + "%";
        return (root, query, cb) -> {
            // Distinct is needed to avoid duplicates from joins
            query.distinct(true);

            Join<ConversationEntity_FCA1, UserEntity_FCA1> participantJoin = root.join("participants", JoinType.LEFT);
            Join<ConversationEntity_FCA1, MessageEntity_FCA1> messageJoin = root.join("messages", JoinType.LEFT);

            Predicate searchInParticipantName = cb.like(cb.lower(participantJoin.get("displayName")), pattern);
            Predicate searchInMessageContent = cb.like(cb.lower(messageJoin.get("content")), pattern);
            
            return cb.or(searchInParticipantName, searchInMessageContent);
        };
    }
}
```
```java
// Mapper to convert entities to DTOs
// File: ConversationMapper_FCA1.java