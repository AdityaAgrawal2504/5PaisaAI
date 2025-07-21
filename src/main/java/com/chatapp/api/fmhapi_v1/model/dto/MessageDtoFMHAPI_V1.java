package com.chatapp.api.fmhapi_v1.model.dto;

import com.chatapp.api.fmhapi_v1.model.enums.MessageStatusFMHAPI_V1;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * DTO for representing a single message.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageDtoFMHAPI_V1 {
    private String id;
    private UUID conversationId;
    private UserSummaryDtoFMHAPI_V1 author;
    private MessageContentDtoFMHAPI_V1 content;
    private MessageStatusFMHAPI_V1 status;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
