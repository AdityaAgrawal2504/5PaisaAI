package com.chatapp.api.fmhapi_v1.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

/**
 * DTO for representing a summary of a user.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSummaryDtoFMHAPI_V1 {
    private UUID id;
    private String displayName;
    private String avatarUrl;
}
