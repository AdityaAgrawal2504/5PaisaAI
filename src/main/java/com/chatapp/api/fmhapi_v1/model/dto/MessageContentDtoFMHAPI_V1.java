package com.chatapp.api.fmhapi_v1.model.dto;

import com.chatapp.api.fmhapi_v1.model.enums.MessageContentTypeFMHAPI_V1;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for representing the content of a message.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MessageContentDtoFMHAPI_V1 {
    private MessageContentTypeFMHAPI_V1 type;
    private String text;
    private String url;
    private String fileName;
}
