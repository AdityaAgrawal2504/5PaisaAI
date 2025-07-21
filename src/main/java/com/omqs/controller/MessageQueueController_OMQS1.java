package com.omqs.controller;

import com.omqs.dto.EnqueueResponseDto_OMQS1;
import com.omqs.dto.MessageAcknowledgementDto_OMQS1;
import com.omqs.dto.MessageDto_OMQS1;
import com.omqs.dto.MessageRequestDto_OMQS1;
import com.omqs.service.OfflineMessageService_OMQS1;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

/**
 * REST controller for handling offline message queueing operations.
 */
@RestController
@RequestMapping("/v1/queues")
@RequiredArgsConstructor
@Validated
public class MessageQueueController_OMQS1 {

    private final OfflineMessageService_OMQS1 messageService;

    /**
     * Endpoint to queue a single message for an offline user.
     */
    @PostMapping("/{userId}/messages")
    public ResponseEntity<EnqueueResponseDto_OMQS1> enqueueMessage(
            @PathVariable UUID userId,
            @Valid @RequestBody MessageRequestDto_OMQS1 messageRequest) {
        EnqueueResponseDto_OMQS1 response = messageService.enqueueMessage(userId, messageRequest);
        return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
    }

    /**
     * Endpoint to retrieve all queued messages for a user.
     */
    @GetMapping("/{userId}/messages")
    public ResponseEntity<Map<UUID, MessageDto_OMQS1>> dequeueMessages(
            @PathVariable UUID userId,
            @RequestParam(defaultValue = "${service.dequeue.default-limit}")
            @Min(value = 1, message = "Limit must be at least 1")
            @Max(value = 500, message = "Limit cannot exceed 500") int limit) {
        Map<UUID, MessageDto_OMQS1> messages = messageService.dequeueMessages(userId, limit);
        return ResponseEntity.ok(messages);
    }

    /**
     * Endpoint to acknowledge receipt of messages, removing them from the queue.
     */
    @PostMapping("/{userId}/acknowledgements")
    public ResponseEntity<Void> acknowledgeMessages(
            @PathVariable UUID userId,
            @Valid @RequestBody MessageAcknowledgementDto_OMQS1 acknowledgement) {
        messageService.acknowledgeMessages(userId, acknowledgement.getMessageIds().keySet());
        return ResponseEntity.noContent().build();
    }
}
```
```java
// src/test/java/com/omqs/service/OfflineMessageServiceImpl_OMQS1Test.java