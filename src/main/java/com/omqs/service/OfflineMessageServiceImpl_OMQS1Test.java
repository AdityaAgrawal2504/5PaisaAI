package com.omqs.service;

import com.omqs.dto.EnqueueResponseDto_OMQS1;
import com.omqs.dto.MessageRequestDto_OMQS1;
import com.omqs.exception.PersistenceOperationException_OMQS1;
import com.omqs.exception.ResourceNotFoundException_OMQS1;
import com.omqs.exception.ValidationException_OMQS1;
import com.omqs.model.OfflineMessage_OMQS1;
import com.omqs.repository.OfflineMessageRepository_OMQS1;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OfflineMessageServiceImpl_OMQS1Test {

    @Mock
    private OfflineMessageRepository_OMQS1 messageRepository;

    @InjectMocks
    private OfflineMessageServiceImpl_OMQS1 messageService;

    private UUID userId;
    private UUID senderId;
    private MessageRequestDto_OMQS1 requestDto;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        senderId = UUID.randomUUID();
        requestDto = new MessageRequestDto_OMQS1();
        requestDto.setRecipientId(userId);
        requestDto.setSenderId(senderId);
        requestDto.setTimestamp(Instant.now());
        requestDto.setPayload(Map.of("content", "hello"));
    }

    @Test
    void enqueueMessage_Success() {
        OfflineMessage_OMQS1 savedMessage = new OfflineMessage_OMQS1();
        savedMessage.setMessageId(UUID.randomUUID());
        savedMessage.setEnqueuedAt(Instant.now());

        when(messageRepository.save(any(OfflineMessage_OMQS1.class))).thenReturn(savedMessage);

        EnqueueResponseDto_OMQS1 response = messageService.enqueueMessage(userId, requestDto);

        assertNotNull(response);
        assertEquals(savedMessage.getMessageId(), response.getMessageId());
        assertNotNull(response.getEnqueuedAt());
        verify(messageRepository).save(any(OfflineMessage_OMQS1.class));
    }

    @Test
    void enqueueMessage_RecipientIdMismatch_ThrowsValidationException() {
        UUID otherUserId = UUID.randomUUID();
        requestDto.setRecipientId(otherUserId);

        Exception exception = assertThrows(ValidationException_OMQS1.class, () -> {
            messageService.enqueueMessage(userId, requestDto);
        });

        assertEquals("Recipient ID in message body must match user ID in URL path.", exception.getMessage());
        verify(messageRepository, never()).save(any());
    }
    
    @Test
    void enqueueMessage_NullPayload_ThrowsValidationException() {
        requestDto.setPayload(null);
        Exception exception = assertThrows(ValidationException_OMQS1.class, () -> {
            messageService.enqueueMessage(userId, requestDto);
        });
        assertEquals("Payload must be a non-empty JSON object.", exception.getMessage());
    }

    @Test
    void enqueueMessage_PersistenceFailure_ThrowsPersistenceException() {
        when(messageRepository.save(any(OfflineMessage_OMQS1.class))).thenThrow(new DataAccessResourceFailureException("DB down"));

        assertThrows(PersistenceOperationException_OMQS1.class, () -> {
            messageService.enqueueMessage(userId, requestDto);
        });
    }

    @Test
    void dequeueMessages_Success_ReturnsMessageMap() {
        OfflineMessage_OMQS1 message1 = new OfflineMessage_OMQS1();
        message1.setMessageId(UUID.randomUUID());
        message1.setRecipientId(userId);

        when(messageRepository.findByRecipientIdOrderByEnqueuedAtAsc(eq(userId), any(Pageable.class)))
                .thenReturn(List.of(message1));

        Map<UUID, ?> messages = messageService.dequeueMessages(userId, 100);

        assertFalse(messages.isEmpty());
        assertEquals(1, messages.size());
        assertTrue(messages.containsKey(message1.getMessageId()));
    }
    
    @Test
    void dequeueMessages_NoMessages_ReturnsEmptyMap() {
        when(messageRepository.findByRecipientIdOrderByEnqueuedAtAsc(eq(userId), any(Pageable.class)))
                .thenReturn(Collections.emptyList());

        Map<UUID, ?> messages = messageService.dequeueMessages(userId, 100);

        assertTrue(messages.isEmpty());
    }

    @Test
    void acknowledgeMessages_Success() {
        Set<UUID> messageIds = Set.of(UUID.randomUUID(), UUID.randomUUID());
        when(messageRepository.countByRecipientIdAndMessageIdIn(userId, messageIds)).thenReturn(2L);
        
        assertDoesNotThrow(() -> {
            messageService.acknowledgeMessages(userId, messageIds);
        });

        verify(messageRepository).deleteByRecipientIdAndMessageIdIn(userId, messageIds);
    }
    
    @Test
    void acknowledgeMessages_EmptySet_DoesNothing() {
         assertDoesNotThrow(() -> {
            messageService.acknowledgeMessages(userId, Collections.emptySet());
        });
        verify(messageRepository, never()).countByRecipientIdAndMessageIdIn(any(), any());
        verify(messageRepository, never()).deleteByRecipientIdAndMessageIdIn(any(), any());
    }

    @Test
    void acknowledgeMessages_MessageNotFound_ThrowsResourceNotFoundException() {
        Set<UUID> messageIds = Set.of(UUID.randomUUID());
        when(messageRepository.countByRecipientIdAndMessageIdIn(userId, messageIds)).thenReturn(0L);

        assertThrows(ResourceNotFoundException_OMQS1.class, () -> {
            messageService.acknowledgeMessages(userId, messageIds);
        });
        verify(messageRepository, never()).deleteByRecipientIdAndMessageIdIn(any(), any());
    }

    @Test
    void purgeOldMessages_Success_DeletesMessages() {
        ArgumentCaptor<Instant> captor = ArgumentCaptor.forClass(Instant.class);
        when(messageRepository.deleteByEnqueuedAtBefore(any(Instant.class))).thenReturn(5);

        messageService.purgeOldMessages();

        verify(messageRepository).deleteByEnqueuedAtBefore(captor.capture());
        Instant cutoff = captor.getValue();
        long days = ChronoUnit.DAYS.between(cutoff, Instant.now());
        assertTrue(days >= 6 && days <= 8); // Should be around 7 days ago
    }
    
    @Test
    void purgeOldMessages_PersistenceFailure_LogsError() {
         when(messageRepository.deleteByEnqueuedAtBefore(any(Instant.class)))
            .thenThrow(new DataAccessResourceFailureException("DB down"));
        
        // Should not throw exception, just log it.
        assertDoesNotThrow(() -> messageService.purgeOldMessages());

        // We can't verify logs easily without more setup, but we ensure no exception propagates.
    }
}
```
```java
// src/test/java/com/omqs/controller/MessageQueueController_OMQS1Test.java