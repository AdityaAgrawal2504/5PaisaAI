package com.omqs.service;

import com.omqs.dto.EnqueueResponseDto_OMQS1;
import com.omqs.dto.MessageDto_OMQS1;
import com.omqs.dto.MessageRequestDto_OMQS1;
import com.omqs.exception.PersistenceOperationException_OMQS1;
import com.omqs.exception.ResourceNotFoundException_OMQS1;
import com.omqs.exception.ValidationException_OMQS1;
import com.omqs.model.OfflineMessage_OMQS1;
import com.omqs.repository.OfflineMessageRepository_OMQS1;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Implementation of the service layer for managing offline messages.
 */
@Service
@RequiredArgsConstructor
@Log4j2
public class OfflineMessageServiceImpl_OMQS1 implements OfflineMessageService_OMQS1 {

    private final OfflineMessageRepository_OMQS1 messageRepository;
    private static final int PURGE_AFTER_DAYS = 7;

    /**
     * Validates and persists a message for a user.
     */
    @Override
    public EnqueueResponseDto_OMQS1 enqueueMessage(UUID userId, MessageRequestDto_OMQS1 messageRequest) {
        if (!userId.equals(messageRequest.getRecipientId())) {
            throw new ValidationException_OMQS1("Recipient ID in message body must match user ID in URL path.");
        }
        if (messageRequest.getPayload() == null || messageRequest.getPayload().isEmpty()) {
            throw new ValidationException_OMQS1("Payload must be a non-empty JSON object.");
        }

        OfflineMessage_OMQS1 message = convertToEntity(messageRequest);
        message.setEnqueuedAt(Instant.now());

        try {
            OfflineMessage_OMQS1 savedMessage = messageRepository.save(message);
            return new EnqueueResponseDto_OMQS1(savedMessage.getMessageId(), savedMessage.getEnqueuedAt());
        } catch (DataAccessException e) {
            throw new PersistenceOperationException_OMQS1("Failed to persist message", e);
        }
    }

    /**
     * Retrieves messages for a user up to the given limit.
     */
    @Override
    public Map<UUID, MessageDto_OMQS1> dequeueMessages(UUID userId, int limit) {
        try {
            Pageable pageable = PageRequest.of(0, limit);
            List<OfflineMessage_OMQS1> messages = messageRepository.findByRecipientIdOrderByEnqueuedAtAsc(userId, pageable);
            return messages.stream()
                .map(this::convertToDto)
                .collect(Collectors.toMap(MessageDto_OMQS1::getMessageId, Function.identity()));
        } catch (DataAccessException e) {
            throw new PersistenceOperationException_OMQS1("Failed to retrieve messages", e);
        }
    }

    /**
     * Acknowledges and deletes messages after verifying their existence.
     */
    @Override
    public void acknowledgeMessages(UUID userId, Set<UUID> messageIds) {
        if (messageIds == null || messageIds.isEmpty()) {
            return; // Nothing to do
        }
        try {
            long foundCount = messageRepository.countByRecipientIdAndMessageIdIn(userId, messageIds);
            if (foundCount != messageIds.size()) {
                throw new ResourceNotFoundException_OMQS1("One or more specified message IDs do not exist in the user's queue.");
            }
            messageRepository.deleteByRecipientIdAndMessageIdIn(userId, messageIds);
        } catch (DataAccessException e) {
            throw new PersistenceOperationException_OMQS1("Failed to delete acknowledged messages", e);
        }
    }

    /**
     * Scheduled task to purge messages older than 7 days. Runs once per day.
     */
    @Override
    @Scheduled(cron = "0 0 2 * * ?") // Run every day at 2 AM
    public void purgeOldMessages() {
        Instant cutoff = Instant.now().minus(PURGE_AFTER_DAYS, ChronoUnit.DAYS);
        try {
            int deletedCount = messageRepository.deleteByEnqueuedAtBefore(cutoff);
            if (deletedCount > 0) {
                log.info("Successfully purged {} old messages from the queue.", deletedCount);
            }
        } catch (DataAccessException e) {
            log.error("Scheduled purge of old messages failed.", e);
            // In a production system, this should trigger an alert.
        }
    }

    private OfflineMessage_OMQS1 convertToEntity(MessageRequestDto_OMQS1 dto) {
        OfflineMessage_OMQS1 entity = new OfflineMessage_OMQS1();
        entity.setSenderId(dto.getSenderId());
        entity.setRecipientId(dto.getRecipientId());
        entity.setPayload(dto.getPayload());
        entity.setTimestamp(dto.getTimestamp());
        return entity;
    }

    private MessageDto_OMQS1 convertToDto(OfflineMessage_OMQS1 entity) {
        MessageDto_OMQS1 dto = new MessageDto_OMQS1();
        dto.setMessageId(entity.getMessageId());
        dto.setSenderId(entity.getSenderId());
        dto.setRecipientId(entity.getRecipientId());
        dto.setPayload(entity.getPayload());
        dto.setTimestamp(entity.getTimestamp());
        dto.setEnqueuedAt(entity.getEnqueuedAt());
        return dto;
    }
}
```
```java
// src/main/java/com/omqs/controller/MessageQueueController_OMQS1.java