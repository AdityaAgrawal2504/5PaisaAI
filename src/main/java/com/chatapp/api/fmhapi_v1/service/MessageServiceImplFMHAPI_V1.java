package com.chatapp.api.fmhapi_v1.service;

import com.chatapp.api.fmhapi_v1.exception.AccessDeniedExceptionFMHAPI_V1;
import com.chatapp.api.fmhapi_v1.exception.InvalidCursorExceptionFMHAPI_V1;
import com.chatapp.api.fmhapi_v1.exception.ResourceNotFoundExceptionFMHAPI_V1;
import com.chatapp.api.fmhapi_v1.mapper.MessageMapperFMHAPI_V1;
import com.chatapp.api.fmhapi_v1.model.dto.FetchMessagesResponseFMHAPI_V1;
import com.chatapp.api.fmhapi_v1.model.dto.MessageDtoFMHAPI_V1;
import com.chatapp.api.fmhapi_v1.model.dto.PaginationInfoDtoFMHAPI_V1;
import com.chatapp.api.fmhapi_v1.model.entity.MessageEntityFMHAPI_V1;
import com.chatapp.api.fmhapi_v1.model.enums.ErrorCodeFMHAPI_V1;
import com.chatapp.api.fmhapi_v1.repository.MessageRepositoryFMHAPI_V1;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of the MessageService interface.
 */
@Service
public class MessageServiceImplFMHAPI_V1 implements MessageServiceFMHAPI_V1 {

    private final MessageRepositoryFMHAPI_V1 messageRepository;
    private final MessageMapperFMHAPI_V1 messageMapper;

    public MessageServiceImplFMHAPI_V1(MessageRepositoryFMHAPI_V1 messageRepository, MessageMapperFMHAPI_V1 messageMapper) {
        this.messageRepository = messageRepository;
        this.messageMapper = messageMapper;
    }

    /**
     * Retrieves message history, handling business logic for authorization and pagination.
     */
    @Override
    @Transactional(readOnly = true)
    public FetchMessagesResponseFMHAPI_V1 getMessageHistory(UUID conversationId, int limit, String before) {
        // In a real app, this would come from the security context (e.g., JWT)
        UUID currentUserId = UUID.fromString("a1b2c3d4-e5f6-7890-1234-567890abcdef");
        validateUserAccess(conversationId, currentUserId);

        // We fetch one more item than the limit to determine if there's a next page.
        PageRequest pageRequest = PageRequest.of(0, limit + 1);
        
        List<MessageEntityFMHAPI_V1> messages;

        if (before != null && !before.isEmpty()) {
            MessageEntityFMHAPI_V1 cursorMessage = messageRepository.findById(before)
                .orElseThrow(() -> new InvalidCursorExceptionFMHAPI_V1("The provided 'before' cursor is invalid or does not exist."));
            
            messages = messageRepository.findByConversationIdAndCreatedAtBefore(
                conversationId, 
                cursorMessage.getCreatedAt(), 
                pageRequest
            );
        } else {
            // First page request
            messages = messageRepository.findByConversationIdOrderByCreatedAtDesc(conversationId, pageRequest);
        }

        boolean hasMore = messages.size() > limit;
        List<MessageEntityFMHAPI_V1> pageContent = hasMore ? messages.subList(0, limit) : messages;

        List<MessageDtoFMHAPI_V1> messageDtos = pageContent.stream()
                .map(messageMapper::toDto)
                .collect(Collectors.toList());

        String nextCursor = null;
        if (hasMore) {
            nextCursor = pageContent.get(pageContent.size() - 1).getId();
        }

        PaginationInfoDtoFMHAPI_V1 paginationInfo = PaginationInfoDtoFMHAPI_V1.builder()
                .nextCursor(nextCursor)
                .hasMore(hasMore)
                .build();

        return FetchMessagesResponseFMHAPI_V1.builder()
                .data(messageDtos)
                .pagination(paginationInfo)
                .build();
    }
    
    /**
     * Validates if the user has access to the conversation.
     * @param conversationId The ID of the conversation.
     * @param userId The ID of the user.
     */
    private void validateUserAccess(UUID conversationId, UUID userId) {
        // This check simulates checking for conversation existence and user participation.
        if (!messageRepository.isUserParticipantInConversation(conversationId, userId)) {
            // Check if conversation even exists to provide a more accurate error.
            if (messageRepository.countByConversationId(conversationId) == 0) {
                 throw new ResourceNotFoundExceptionFMHAPI_V1(
                    ErrorCodeFMHAPI_V1.CONVERSATION_NOT_FOUND,
                    "The requested conversation does not exist."
                );
            }
            throw new AccessDeniedExceptionFMHAPI_V1("You do not have permission to access this resource.");
        }
    }
    
    // Add a helper method in the repository interface to count messages
    // to check for conversation existence.
}

// Add to MessageRepositoryFMHAPI_V1 interface:
// long countByConversationId(UUID conversationId);
