package com.chatapp.api.fmhapi_v1.mapper;

import com.chatapp.api.fmhapi_v1.model.dto.MessageContentDtoFMHAPI_V1;
import com.chatapp.api.fmhapi_v1.model.dto.MessageDtoFMHAPI_V1;
import com.chatapp.api.fmhapi_v1.model.dto.UserSummaryDtoFMHAPI_V1;
import com.chatapp.api.fmhapi_v1.model.entity.MessageEntityFMHAPI_V1;
import com.chatapp.api.fmhapi_v1.model.entity.UserEntityFMHAPI_V1;
import org.springframework.stereotype.Component;

/**
 * Mapper to convert between MessageEntity and MessageDto.
 */
@Component
public class MessageMapperFMHAPI_V1 {

    /**
     * Converts a MessageEntity to a MessageDto.
     * @param entity The MessageEntity to convert.
     * @return The resulting MessageDto.
     */
    public MessageDtoFMHAPI_V1 toDto(MessageEntityFMHAPI_V1 entity) {
        if (entity == null) {
            return null;
        }
        return MessageDtoFMHAPI_V1.builder()
                .id(entity.getId())
                .conversationId(entity.getConversationId())
                .author(toUserSummaryDto(entity.getAuthor()))
                .content(toMessageContentDto(entity))
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    private UserSummaryDtoFMHAPI_V1 toUserSummaryDto(UserEntityFMHAPI_V1 userEntity) {
        if (userEntity == null) {
            return null;
        }
        return UserSummaryDtoFMHAPI_V1.builder()
                .id(userEntity.getId())
                .displayName(userEntity.getDisplayName())
                .avatarUrl(userEntity.getAvatarUrl())
                .build();
    }

    private MessageContentDtoFMHAPI_V1 toMessageContentDto(MessageEntityFMHAPI_V1 messageEntity) {
        return MessageContentDtoFMHAPI_V1.builder()
                .type(messageEntity.getContentType())
                .text(messageEntity.getTextContent())
                .url(messageEntity.getMediaUrl())
                .fileName(messageEntity.getFileName())
                .build();
    }
}

