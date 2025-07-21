package com.omqs.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.omqs.dto.EnqueueResponseDto_OMQS1;
import com.omqs.dto.MessageAcknowledgementDto_OMQS1;
import com.omqs.dto.MessageRequestDto_OMQS1;
import com.omqs.service.OfflineMessageService_OMQS1;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MessageQueueController_OMQS1.class)
class MessageQueueController_OMQS1Test {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OfflineMessageService_OMQS1 messageService;

    private final UUID userId = UUID.randomUUID();
    private final String V1_PATH = "/v1/queues/" + userId;

    @Test
    void enqueueMessage_ValidRequest_Returns202Accepted() throws Exception {
        MessageRequestDto_OMQS1 requestDto = new MessageRequestDto_OMQS1();
        requestDto.setRecipientId(userId);
        requestDto.setSenderId(UUID.randomUUID());
        requestDto.setTimestamp(Instant.now());
        requestDto.setPayload(Map.of("test", "data"));

        EnqueueResponseDto_OMQS1 serviceResponse = new EnqueueResponseDto_OMQS1(UUID.randomUUID(), Instant.now());

        when(messageService.enqueueMessage(eq(userId), any(MessageRequestDto_OMQS1.class)))
                .thenReturn(serviceResponse);

        mockMvc.perform(post(V1_PATH + "/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.messageId").value(serviceResponse.getMessageId().toString()))
                .andExpect(jsonPath("$.enqueuedAt").exists());
    }

    @Test
    void enqueueMessage_InvalidBody_Returns400BadRequest() throws Exception {
        MessageRequestDto_OMQS1 requestDto = new MessageRequestDto_OMQS1(); // Missing required fields
        requestDto.setRecipientId(userId);

        mockMvc.perform(post(V1_PATH + "/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"));
    }

    @Test
    void dequeueMessages_ValidRequest_Returns200OK() throws Exception {
        when(messageService.dequeueMessages(userId, 50)).thenReturn(Collections.emptyMap());

        mockMvc.perform(get(V1_PATH + "/messages")
                        .param("limit", "50"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void dequeueMessages_InvalidLimit_Returns400BadRequest() throws Exception {
        mockMvc.perform(get(V1_PATH + "/messages")
                        .param("limit", "1000")) // Exceeds max
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.details.limit").value("Limit cannot exceed 500"));
    }

    @Test
    void dequeueMessages_InvalidUserIdFormat_Returns400BadRequest() throws Exception {
        mockMvc.perform(get("/v1/queues/not-a-uuid/messages"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"));
    }


    @Test
    void acknowledgeMessages_ValidRequest_Returns204NoContent() throws Exception {
        MessageAcknowledgementDto_OMQS1 ackDto = new MessageAcknowledgementDto_OMQS1();
        ackDto.setMessageIds(Map.of(UUID.randomUUID(), true));

        mockMvc.perform(post(V1_PATH + "/acknowledgements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ackDto)))
                .andExpect(status().isNoContent());
    }

    @Test
    void acknowledgeMessages_EmptyBody_Returns400BadRequest() throws Exception {
        MessageAcknowledgementDto_OMQS1 ackDto = new MessageAcknowledgementDto_OMQS1();
        ackDto.setMessageIds(Collections.emptyMap()); // Fails @NotEmpty

        mockMvc.perform(post(V1_PATH + "/acknowledgements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ackDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.details.messageIds").value("messageIds object must not be empty."));
    }
}
```