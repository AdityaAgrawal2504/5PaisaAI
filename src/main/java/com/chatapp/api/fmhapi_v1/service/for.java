package com.chatapp.api.fmhapi_v1.service;

import com.chatapp.api.fmhapi_v1.model.dto.FetchMessagesResponseFMHAPI_V1;
import java.util.UUID;

/**
 * Service interface for message-related operations.
 */
public interface MessageServiceFMHAPI_V1 {

    /**
     * Retrieves a paginated list of messages for a given conversation.
     * @param conversationId The ID of the conversation.
     * @param limit The maximum number of messages to return.
     * @param before The cursor (message ID) to fetch messages before.
     * @return A response object containing the list of messages and pagination info.
     */
    FetchMessagesResponseFMHAPI_V1 getMessageHistory(UUID conversationId, int limit, String before);
}

