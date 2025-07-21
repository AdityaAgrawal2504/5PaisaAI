package com.chatapp.api.fmhapi_v1.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for cursor-based pagination information.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaginationInfoDtoFMHAPI_V1 {
    private String nextCursor;
    private boolean hasMore;
}
