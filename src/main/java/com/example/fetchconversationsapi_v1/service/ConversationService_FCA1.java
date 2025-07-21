package com.example.fetchconversationsapi_v1.service;

import com.example.fetchconversationsapi_v1.dto.FetchConversationsRequestDto_FCA1;
import com.example.fetchconversationsapi_v1.dto.PaginatedConversationsResponseDto_FCA1;
import com.example.fetchconversationsapi_v1.enums.SortOrder_FCA1;
import com.example.fetchconversationsapi_v1.exception.UnauthorizedException_FCA1;
import com.example.fetchconversationsapi_v1.mapper.ConversationMapper_FCA1;
import com.example.fetchconversationsapi_v1.model.ConversationEntity_FCA1;
import com.example.fetchconversationsapi_v1.repository.ConversationRepository_FCA1;
import com.example.fetchconversationsapi_v1.repository.ConversationSpecification_FCA1;
import com.example.fetchconversationsapi_v1.security.SecurityUtil_FCA1;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ConversationService_FCA1 {

    private final ConversationRepository_FCA1 conversationRepository;
    private final ConversationMapper_FCA1 conversationMapper;
    private final SecurityUtil_FCA1 securityUtil;

    /**
     * Fetches a paginated list of conversations for the authenticated user based on filter criteria.
     * @param requestDto DTO containing pagination, sorting, and filtering parameters.
     * @return A paginated list of conversation summaries.
     */
    @Transactional(readOnly = true)
    public PaginatedConversationsResponseDto_FCA1 fetchConversations(FetchConversationsRequestDto_FCA1 requestDto) {
        UUID currentUserId = securityUtil.getCurrentUserId()
                .orElseThrow(() -> new UnauthorizedException_FCA1("User is not authenticated."));

        // Build Pageable
        Sort.Direction direction = requestDto.getSortOrder() == SortOrder_FCA1.ASC ? Sort.Direction.ASC : Sort.Direction.DESC;
        String sortByField = requestDto.getSortBy().getEntityField();
        Pageable pageable = PageRequest.of(requestDto.getPage() - 1, requestDto.getPageSize(), Sort.by(direction, sortByField));

        // Build Specification
        Specification<ConversationEntity_FCA1> spec = Specification.where(ConversationSpecification_FCA1.hasParticipant(currentUserId));
        if (requestDto.getSeen() != null) {
            spec = spec.and(ConversationSpecification_FCA1.isSeen(requestDto.getSeen(), currentUserId));
        }
        if (requestDto.getSearchQuery() != null && !requestDto.getSearchQuery().isBlank()) {
            spec = spec.and(ConversationSpecification_FCA1.hasSearchQuery(requestDto.getSearchQuery()));
        }

        // Fetch data
        Page<ConversationEntity_FCA1> conversationPage = conversationRepository.findAll(spec, pageable);

        // Map to DTO
        return conversationMapper.toPaginatedResponseDto(conversationPage, currentUserId);
    }
}
```
```java
// Controller for the API endpoint
// File: ConversationController_FCA1.java