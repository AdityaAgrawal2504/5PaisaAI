package com.example.fetchconversationsapi_v1.controller;

import com.example.fetchconversationsapi_v1.dto.FetchConversationsRequestDto_FCA1;
import com.example.fetchconversationsapi_v1.dto.PaginatedConversationsResponseDto_FCA1;
import com.example.fetchconversationsapi_v1.service.ConversationService_FCA1;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/conversations")
@RequiredArgsConstructor
@Tag(name = "Conversations", description = "APIs for managing conversations")
@SecurityRequirement(name = "BearerAuth")
public class ConversationController_FCA1 {

    private final ConversationService_FCA1 conversationService;

    /**
     * Retrieves a paginated list of the authenticated user's conversations.
     * Supports filtering, sorting, and searching.
     * @param requestDto DTO containing validated query parameters.
     * @return A ResponseEntity with the paginated list of conversations.
     */
    @GetMapping
    @Operation(
        summary = "Fetch Conversations",
        description = "Retrieves a paginated list of the authenticated user's conversations. Supports filtering, sorting, and searching."
    )
    public ResponseEntity<PaginatedConversationsResponseDto_FCA1> fetchConversations(
            @Valid FetchConversationsRequestDto_FCA1 requestDto) {
        
        PaginatedConversationsResponseDto_FCA1 response = conversationService.fetchConversations(requestDto);
        return ResponseEntity.ok(response);
    }
}
```
```java
// Custom Unauthorized Exception
// File: UnauthorizedException_FCA1.java