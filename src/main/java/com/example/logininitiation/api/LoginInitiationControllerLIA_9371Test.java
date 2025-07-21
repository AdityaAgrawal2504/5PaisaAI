package com.example.logininitiation.api;

import com.example.logininitiation.dto.LoginInitiationRequestLIA_9371;
import com.example.logininitiation.dto.LoginInitiationResponseLIA_9371;
import com.example.logininitiation.exception.AuthenticationFailedExceptionLIA_9371;
import com.example.logininitiation.exception.OtpServiceExceptionLIA_9371;
import com.example.logininitiation.service.IAuthenticationServiceLIA_9371;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
    controllers = LoginInitiationControllerLIA_9371.class,
    excludeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = EnableWebSecurity.class)
)
class LoginInitiationControllerLIA_9371Test {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private IAuthenticationServiceLIA_9371 authenticationService;

    @Test
    void initiateLogin_shouldReturn200_whenRequestIsValid() throws Exception {
        LoginInitiationRequestLIA_9371 request = new LoginInitiationRequestLIA_9371("1234567890", "ValidPass!123");
        LoginInitiationResponseLIA_9371 response = new LoginInitiationResponseLIA_9371("An OTP has been sent to your registered phone number.", UUID.randomUUID().toString());

        given(authenticationService.initiateLogin(any(LoginInitiationRequestLIA_9371.class))).willReturn(response);

        mockMvc.perform(post("/api/auth/initiate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(response.getMessage()))
                .andExpect(jsonPath("$.correlationId").value(response.getCorrelationId()));
    }

    @Test
    void initiateLogin_shouldReturn400_whenPhoneNumberIsInvalid() throws Exception {
        LoginInitiationRequestLIA_9371 request = new LoginInitiationRequestLIA_9371("123", "ValidPass!123");

        mockMvc.perform(post("/api/auth/initiate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("INVALID_INPUT"))
                .andExpect(jsonPath("$.message").value("Phone number must be exactly 10 digits."));
    }

    @Test
    void initiateLogin_shouldReturn400_whenPasswordIsTooShort() throws Exception {
        LoginInitiationRequestLIA_9371 request = new LoginInitiationRequestLIA_9371("1234567890", "short");

        mockMvc.perform(post("/api/auth/initiate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("INVALID_INPUT"))
                .andExpect(jsonPath("$.message").value("Password must be between 8 and 128 characters."));
    }

    @Test
    void initiateLogin_shouldReturn401_whenAuthenticationFails() throws Exception {
        LoginInitiationRequestLIA_9371 request = new LoginInitiationRequestLIA_9371("1234567890", "WrongPassword123");
        String errorMessage = "The credentials provided are invalid.";
        given(authenticationService.initiateLogin(any(LoginInitiationRequestLIA_9371.class)))
                .willThrow(new AuthenticationFailedExceptionLIA_9371(errorMessage));

        mockMvc.perform(post("/api/auth/initiate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.errorCode").value("AUTHENTICATION_FAILED"))
                .andExpect(jsonPath("$.message").value(errorMessage));
    }

    @Test
    void initiateLogin_shouldReturn500_whenOtpServiceFails() throws Exception {
        LoginInitiationRequestLIA_9371 request = new LoginInitiationRequestLIA_9371("1234567890", "ValidPass!123");
        String errorMessage = "Downstream OTP service is unavailable.";
        given(authenticationService.initiateLogin(any(LoginInitiationRequestLIA_9371.class)))
                .willThrow(new OtpServiceExceptionLIA_9371(errorMessage));

        mockMvc.perform(post("/api/auth/initiate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.errorCode").value("OTP_SERVICE_FAILURE"))
                .andExpect(jsonPath("$.message").value(errorMessage));
    }
}
```