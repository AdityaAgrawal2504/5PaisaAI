package com.chatapp.api.fmhapi_v1.controller;

import com.chatapp.api.fmhapi_v1.exception.GlobalExceptionHandlerFMHAPI_V1;
import com.chatapp.api.fmhapi_v1.exception.ResourceNotFoundExceptionFMHAPI_V1;
import com.chatapp.api.fmhapi_v1.model.dto.FetchMessagesResponseFMHAPI_V1;
import com.chatapp.api.fmhapi_v1.model.enums.ErrorCodeFMHAPI_V1;
import com.chatapp.api.fmhapi_v1.service.MessageServiceFMHAPI_V1;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MessageControllerFMHAPI_V1.class)
@Import(GlobalExceptionHandlerFMHAPI_V1.class)
class MessageControllerFMHAPI_V1Test {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MessageServiceFMHAPI_V1 messageService;

    private UUID conversationId;

    @BeforeEach
    void setUp() {
        conversationId = UUID.randomUUID();
    }

    @Test
    void getMessageHistory_validRequest_shouldReturn200() throws Exception {
        FetchMessagesResponseFMHAPI_V1 mockResponse = FetchMessagesResponseFMHAPI_V1.builder()
                .data(Collections.emptyList())
                .pagination(new com.chatapp.api.fmhapi_v1.model.dto.PaginationInfoDtoFMHAPI_V1(null, false))
                .build();

        when(messageService.getMessageHistory(eq(conversationId), eq(25), any()))
                .thenReturn(mockResponse);

        mockMvc.perform(get("/api/conversations/{conversationId}/messages", conversationId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.pagination.hasMore", is(false)));
    }

    @Test
    void getMessageHistory_invalidUuid_shouldReturn400() throws Exception {
        mockMvc.perform(get("/api/conversations/{conversationId}/messages", "not-a-uuid"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code", is("INVALID_PATH_PARAMETER")))
                .andExpect(jsonPath("$.message", is("Path parameter 'conversationId' has an invalid format.")));
    }
    
    @Test
    void getMessageHistory_limitTooHigh_shouldReturn400() throws Exception {
        mockMvc.perform(get("/api/conversations/{conversationId}/messages", conversationId)
                        .param("limit", "101"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code", is("INVALID_QUERY_PARAMETER")));
    }

    @Test
    void getMessageHistory_limitTooLow_shouldReturn400() throws Exception {
        mockMvc.perform(get("/api/conversations/{conversationId}/messages", conversationId)
                        .param("limit", "0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code", is("INVALID_QUERY_PARAMETER")));
    }

    @Test
    void getMessageHistory_conversationNotFound_shouldReturn404() throws Exception {
        when(messageService.getMessageHistory(any(UUID.class), anyInt(), anyString()))
                .thenThrow(new ResourceNotFoundExceptionFMHAPI_V1(
                        ErrorCodeFMHAPI_V1.CONVERSATION_NOT_FOUND,
                        "Conversation not found"
                ));

        mockMvc.perform(get("/api/conversations/{conversationId}/messages", conversationId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code", is("CONVERSATION_NOT_FOUND")))
                .andExpect(jsonPath("$.message", is("Conversation not found")));
    }
}