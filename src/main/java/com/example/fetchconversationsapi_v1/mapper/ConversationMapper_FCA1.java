package com.example.fetchconversationsapi_v1.mapper;

import com.example.fetchconversationsapi_v1.dto.*;
import com.example.fetchconversationsapi_v1.model.ConversationEntity_FCA1;
import com.example.fetchconversationsapi_v1.model.MessageEntity_FCA1;
import com.example.fetchconversationsapi_v1.model.UserConversationStatusEntity_FCA1;
import com.example.fetchconversationsapi_v1.model.UserEntity_FCA1;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class ConversationMapper_FCA1 {

    /**
     * Maps a Page of Conversation entities to a PaginatedConversationsResponse DTO.
     */
    public PaginatedConversationsResponseDto_FCA1 toPaginatedResponseDto(Page<ConversationEntity_FCA1> page, UUID currentUserId) {
        List<ConversationSummaryDto_FCA1> summaries = page.getContent().stream()
                .map(convo -> toSummaryDto(convo, currentUserId))
                .collect(Collectors.toList());

        PaginationInfoDto_FCA1 paginationInfo = PaginationInfoDto_FCA1.builder()
                .currentPage(page.getNumber() + 1) // Page is 0-indexed
                .pageSize(page.getSize())
                .totalPages(page.getTotalPages())
                .totalItems(page.getTotalElements())
                .build();

        return PaginatedConversationsResponseDto_FCA1.builder()
                .data(summaries)
                .pagination(paginationInfo)
                .build();
    }
    
    /**
     * Maps a single Conversation entity to a ConversationSummary DTO.
     */
    public ConversationSummaryDto_FCA1 toSummaryDto(ConversationEntity_FCA1 convo, UUID currentUserId) {
        UserConversationStatusEntity_FCA1 status = getUserStatus(convo, currentUserId);
        
        return ConversationSummaryDto_FCA1.builder()
                .id(convo.getId())
                .title(generateTitle(convo, currentUserId))
                .participants(convo.getParticipants().stream().map(this::toParticipantDto).collect(Collectors.toList()))
                .lastMessage(convo.getLastMessage() != null ? toMessageSnippetDto(convo.getLastMessage()) : null)
                .unreadCount(status != null ? status.getUnreadCount() : 0)
                .isSeen(isConversationSeen(convo, status))
                .createdAt(convo.getCreatedAt())
                .updatedAt(convo.getUpdatedAt())
                .build();
    }
    
    private ParticipantDto_FCA1 toParticipantDto(UserEntity_FCA1 user) {
        return ParticipantDto_FCA1.builder()
                .userId(user.getId())
                .displayName(user.getDisplayName())
                .avatarUrl(user.getAvatarUrl())
                .build();
    }
    
    private MessageSnippetDto_FCA1 toMessageSnippetDto(MessageEntity_FCA1 message) {
        String text = message.getContent();
        if (text != null && text.length() > 100) {
            text = text.substring(0, 100) + "...";
        }
        return MessageSnippetDto_FCA1.builder()
                .id(message.getId())
                .senderId(message.getSender().getId())
                .text(text)
                .timestamp(message.getCreatedAt())
                .build();
    }
    
    private String generateTitle(ConversationEntity_FCA1 convo, UUID currentUserId) {
        if (convo.getTitle() != null && !convo.getTitle().isBlank()) {
            return convo.getTitle();
        }
        return convo.getParticipants().stream()
                .filter(p -> !p.getId().equals(currentUserId))
                .map(UserEntity_FCA1::getDisplayName)
                .collect(Collectors.joining(", "));
    }

    private UserConversationStatusEntity_FCA1 getUserStatus(ConversationEntity_FCA1 convo, UUID currentUserId) {
        return convo.getUserStatuses().stream()
                .filter(s -> s.getUser().getId().equals(currentUserId))
                .findFirst()
                .orElse(null);
    }
    
    private boolean isConversationSeen(ConversationEntity_FCA1 convo, UserConversationStatusEntity_FCA1 status) {
        if (convo.getLastMessage() == null) {
            return true; // No messages, so it's "seen"
        }
        if (status == null || status.getLastSeenMessage() == null) {
            return false; // Status not tracked or never seen, so it's "unseen"
        }
        // Seen if the last seen message is the same as or newer than the conversation's last message
        return !status.getLastSeenMessage().getCreatedAt().isBefore(convo.getLastMessage().getCreatedAt());
    }
}
```
```java
// Service layer for conversation logic
// File: ConversationService_FCA1.java