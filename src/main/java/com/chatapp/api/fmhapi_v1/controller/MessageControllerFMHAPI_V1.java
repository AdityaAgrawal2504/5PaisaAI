package com.chatapp.api.fmhapi_v1.controller;

import com.chatapp.api.fmhapi_v1.model.dto.FetchMessagesResponseFMHAPI_V1;
import com.chatapp.api.fmhapi_v1.service.MessageServiceFMHAPI_V1;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import java.util.UUID;

/**
 * Controller for handling message history requests.
 */
@RestController
@RequestMapping("/api/conversations")
@Validated
public class MessageControllerFMHAPI_V1 {

    private final MessageServiceFMHAPI_V1 messageService;
    private static final String UUID_REGEX = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";

    public MessageControllerFMHAPI_V1(MessageServiceFMHAPI_V1 messageService) {
        this.messageService = messageService;
    }

    /**
     * GET /api/conversations/{conversationId}/messages
     * Retrieves paginated message history for a specific conversation.
     * @param conversationId The UUID of the conversation.
     * @param limit The maximum number of messages to return.
     * @param before A cursor (message ID) for pagination.
     * @return A ResponseEntity containing the list of messages and pagination info.
     */
    @GetMapping("/{conversationId}/messages")
    public ResponseEntity<FetchMessagesResponseFMHAPI_V1> getMessageHistory(
            @PathVariable @Pattern(regexp = UUID_REGEX, message = "Path parameter 'conversationId' has an invalid format.") String conversationId,
            @RequestParam(defaultValue = "25") @Min(1) @Max(100) int limit,
            @RequestParam(required = false) String before) {

        FetchMessagesResponseFMHAPI_V1 response = messageService.getMessageHistory(UUID.fromString(conversationId), limit, before);
        return ResponseEntity.ok(response);
    }
}
