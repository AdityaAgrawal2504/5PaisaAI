package com.example.loginverification.controller;

import com.example.loginverification.dto.LoginVerificationRequest_LVA1;
import com.example.loginverification.dto.LoginVerificationResponse_LVA1;
import com.example.loginverification.exception.InvalidOtpException_LVA1;
import com.example.loginverification.exception.RateLimitException_LVA1;
import com.example.loginverification.exception.ResourceNotFoundException_LVA1;
import com.example.loginverification.logging.LoggingService_LVA1;
import com.example.loginverification.service.LoginVerificationService_LVA1;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LoginVerificationController_LVA1.class)
class LoginVerificationControllerTest_LVA1 {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private LoginVerificationService_LVA1 loginVerificationService;
    
    @MockBean
    private LoggingService_LVA1 loggingService; // Mock this as it's injected into the controller

    private final String API_URL = "/api/auth/verify";

    @Test
    void verify_Success_Returns200() throws Exception {
        LoginVerificationRequest_LVA1 request = new LoginVerificationRequest_LVA1("+14155552671", "123456");
        LoginVerificationResponse_LVA1 response = new LoginVerificationResponse_LVA1("test.jwt.token", "Bearer", 3600);

        when(loginVerificationService.verifyLogin(any(LoginVerificationRequest_LVA1.class))).thenReturn(response);

        mockMvc.perform(post(API_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authToken").value("test.jwt.token"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.expiresIn").value(3600));
    }
    
    @Test
    void verify_InvalidPhoneNumberFormat_Returns400() throws Exception {
        LoginVerificationRequest_LVA1 request = new LoginVerificationRequest_LVA1("123", "123456");

        mockMvc.perform(post(API_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"));
    }

    @Test
    void verify_InvalidOtpFormat_Returns400() throws Exception {
        LoginVerificationRequest_LVA1 request = new LoginVerificationRequest_LVA1("+14155552671", "123");

        mockMvc.perform(post(API_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"));
    }
    
    @Test
    void verify_InvalidOtp_Returns401() throws Exception {
        LoginVerificationRequest_LVA1 request = new LoginVerificationRequest_LVA1("+14155552671", "000000");

        when(loginVerificationService.verifyLogin(any(LoginVerificationRequest_LVA1.class))).thenThrow(new InvalidOtpException_LVA1());

        mockMvc.perform(post(API_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.errorCode").value("INVALID_OTP"));
    }

    @Test
    void verify_UserNotFound_Returns404() throws Exception {
        LoginVerificationRequest_LVA1 request = new LoginVerificationRequest_LVA1("+19998887777", "123456");

        when(loginVerificationService.verifyLogin(any(LoginVerificationRequest_LVA1.class))).thenThrow(new ResourceNotFoundException_LVA1());

        mockMvc.perform(post(API_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value("USER_NOT_FOUND"));
    }

    @Test
    void verify_RateLimited_Returns429() throws Exception {
        LoginVerificationRequest_LVA1 request = new LoginVerificationRequest_LVA1("+14155552671", "123456");
        
        when(loginVerificationService.verifyLogin(any(LoginVerificationRequest_LVA1.class))).thenThrow(new RateLimitException_LVA1());

        mockMvc.perform(post(API_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isTooManyRequests())
                .andExpect(jsonPath("$.errorCode").value("TOO_MANY_ATTEMPTS"));
    }
}
```