package com.chatapp.api.fmhapi_v1.service;

import com.chatapp.api.fmhapi_v1.exception.AccessDeniedExceptionFMHAPI_V1;
import com.chatapp.api.fmhapi_v1.exception.InvalidCursorExceptionFMHAPI_V1;
import com.chatapp.api.fmhapi_v1.exception.ResourceNotFoundExceptionFMHAPI_V1;
import com.chatapp.api.fmhapi_v1.mapper.MessageMapperFMHAPI_V1;
import com.chatapp.api.fmhapi_v1.model.dto.FetchMessagesResponseFMHAPI_V1;
import com.chatapp.api.fmhapi_v1.model.entity.MessageEntityFMHAPI_V1;
import com.chatapp.api.fmhapi_v1.repository.MessageRepositoryFMHAPI_V1;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MessageServiceImplFMHAPI_V1Test {

    @Mock
    private MessageRepositoryFMHAPI_V1 messageRepository;

    @Mock
    private MessageMapperFMHAPI_V1 messageMapper;

    @InjectMocks
    private MessageServiceImplFMHAPI_V1 messageService;

    private UUID conversationId;
    private UUID userId;

    @BeforeEach
    void setUp() {
        conversationId = UUID.randomUUID();
        userId = UUID.fromString("a1b2c3d4-e5f6-7890-1234-567890abcdef");
        when(messageRepository.isUserParticipantInConversation(eq(conversationId), any(UUID.class))).thenReturn(true);
    }

    private List<MessageEntityFMHAPI_V1> generateMessages(int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> MessageEntityFMHAPI_V1.builder()
                        .id("msg_" + i)
                        .conversationId(conversationId)
                        .createdAt(OffsetDateTime.now().minusHours(i))
                        .build())
                .collect(Collectors.toList());
    }

    @Test
    void getMessageHistory_firstPage_shouldReturnMessagesAndPagination() {
        int limit = 10;
        List<MessageEntityFMHAPI_V1> messages = generateMessages(limit + 1);
        when(messageRepository.findByConversationIdOrderByCreatedAtDesc(eq(conversationId), any(PageRequest.class)))
                .thenReturn(messages);
        when(messageMapper.toDto(any(MessageEntityFMHAPI_V1.class))).thenAnswer(i -> {
            MessageEntityFMHAPI_V1 entity = i.getArgument(0);
            return new com.chatapp.api.fmhapi_v1.model.dto.MessageDtoFMHAPI_V1(entity.getId(), null, null, null, null, null, null);
        });

        FetchMessagesResponseFMHAPI_V1 response = messageService.getMessageHistory(conversationId, limit, null);

        assertNotNull(response);
        assertEquals(limit, response.getData().size());
        assertTrue(response.getPagination().isHasMore());
        assertNotNull(response.getPagination().getNextCursor());
        assertEquals("msg_9", response.getPagination().getNextCursor());
    }

    @Test
    void getMessageHistory_withBeforeCursor_shouldReturnOlderMessages() {
        int limit = 10;
        String cursorId = "msg_cursor";
        OffsetDateTime cursorTimestamp = OffsetDateTime.now();
        MessageEntityFMHAPI_V1 cursorMessage = MessageEntityFMHAPI_V1.builder().id(cursorId).createdAt(cursorTimestamp).build();
        
        when(messageRepository.findById(cursorId)).thenReturn(Optional.of(cursorMessage));
        List<MessageEntityFMHAPI_V1> messages = generateMessages(limit); // Less than limit + 1
        when(messageRepository.findByConversationIdAndCreatedAtBefore(eq(conversationId), eq(cursorTimestamp), any(PageRequest.class)))
                .thenReturn(messages);
        when(messageMapper.toDto(any(MessageEntityFMHAPI_V1.class))).thenAnswer(i -> {
            MessageEntityFMHAPI_V1 entity = i.getArgument(0);
            return new com.chatapp.api.fmhapi_v1.model.dto.MessageDtoFMHAPI_V1(entity.getId(), null, null, null, null, null, null);
        });
        
        FetchMessagesResponseFMHAPI_V1 response = messageService.getMessageHistory(conversationId, limit, cursorId);

        assertNotNull(response);
        assertEquals(limit, response.getData().size());
        assertFalse(response.getPagination().isHasMore());
        assertNull(response.getPagination().getNextCursor());
    }

    @Test
    void getMessageHistory_invalidCursor_shouldThrowInvalidCursorException() {
        String invalidCursor = "invalid_cursor";
        when(messageRepository.findById(invalidCursor)).thenReturn(Optional.empty());

        assertThrows(InvalidCursorExceptionFMHAPI_V1.class, () ->
                messageService.getMessageHistory(conversationId, 10, invalidCursor));
    }

    @Test
    void getMessageHistory_conversationNotFound_shouldThrowResourceNotFoundException() {
        UUID nonExistentConversationId = UUID.randomUUID();
        when(messageRepository.isUserParticipantInConversation(eq(nonExistentConversationId), any(UUID.class))).thenReturn(false);
        when(messageRepository.countByConversationId(nonExistentConversationId)).thenReturn(0L);

        assertThrows(ResourceNotFoundExceptionFMHAPI_V1.class, () ->
                messageService.getMessageHistory(nonExistentConversationId, 10, null));
    }

    @Test
    void getMessageHistory_accessDenied_shouldThrowAccessDeniedException() {
        when(messageRepository.isUserParticipantInConversation(eq(conversationId), any(UUID.class))).thenReturn(false);
        // Simulate that the conversation exists by returning a count > 0
        when(messageRepository.countByConversationId(conversationId)).thenReturn(5L);

        assertThrows(AccessDeniedExceptionFMHAPI_V1.class, () ->
                messageService.getMessageHistory(conversationId, 10, null));
    }
}
