package com.chatapp.api.fmhapi_v1.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * DTO for the paginated response of fetching messages.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FetchMessagesResponseFMHAPI_V1 {
    private List<MessageDtoFMHAPI_V1> data;
    private PaginationInfoDtoFMHAPI_V1 pagination;
}
