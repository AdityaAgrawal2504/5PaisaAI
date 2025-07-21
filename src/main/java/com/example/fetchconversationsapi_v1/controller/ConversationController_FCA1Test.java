package com.example.fetchconversationsapi_v1.controller;

import com.example.fetchconversationsapi_v1.config.SecurityConfig_FCA1;
import com.example.fetchconversationsapi_v1.dto.FetchConversationsRequestDto_FCA1;
import com.example.fetchconversationsapi_v1.dto.PaginatedConversationsResponseDto_FCA1;
import com.example.fetchconversationsapi_v1.service.ConversationService_FCA1;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConversationController_FCA1.class)
@Import(SecurityConfig_FCA1.class)
class ConversationController_FCA1Test {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ConversationService_FCA1 conversationService;

    @Test
    @WithMockUser
    void fetchConversations_shouldReturn200_whenRequestIsValid() throws Exception {
        PaginatedConversationsResponseDto_FCA1 response = PaginatedConversationsResponseDto_FCA1.builder()
                .data(Collections.emptyList())
                .build();
        when(conversationService.fetchConversations(any(FetchConversationsRequestDto_FCA1.class))).thenReturn(response);

        mockMvc.perform(get("/api/conversations")
                        .param("page", "1")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @WithMockUser
    void fetchConversations_shouldReturn400_whenPageSizeIsInvalid() throws Exception {
        mockMvc.perform(get("/api/conversations")
                        .param("pageSize", "200"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void fetchConversations_shouldReturn400_whenSortByIsInvalid() throws Exception {
        mockMvc.perform(get("/api/conversations")
                        .param("sortBy", "invalidSortField"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void fetchConversations_shouldReturn401_whenUserIsUnauthenticated() throws Exception {
        mockMvc.perform(get("/api/conversations"))
                .andExpect(status().isUnauthorized());
    }
}
```
```java
// Unit Test for ConversationService
// File: ConversationService_FCA1Test.java