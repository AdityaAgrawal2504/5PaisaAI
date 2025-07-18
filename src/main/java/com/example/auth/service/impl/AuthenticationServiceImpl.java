package com.example.auth.service.impl;

import com.example.auth.dto.ApiError;
import com.example.auth.dto.LoginRequest;
import com.example.auth.dto.LoginResponse;
import com.example.auth.enums.ErrorCode;
import com.example.auth.exception.ApiException;
import com.example.auth.model.User;
import com.example.auth.service.IAuthenticationService;
import com.example.auth.service.IOTPService;
import com.example.auth.service.ITokenService;
import com.example.auth.service.IUserService;
import com.example.auth.util.logging.Loggable;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Implementation of IAuthenticationService orchestrating the login flow.
 */
@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements IAuthenticationService {

    private final IUserService userService;
    private final IOTPService otpService;
    private final ITokenService tokenService;

    /*
     * mermaid
     * sequenceDiagram
     *     participant Client
     *     participant LoginController
     *     participant AuthenticationService
     *     participant UserService
     *     participant OTPService
     *     participant TokenService
     *
     *     Client->>+LoginController: POST /login (phoneNumber, password)
     *     LoginController->>+AuthenticationService: login(request)
     *     AuthenticationService->>+UserService: findByPhoneNumber(phoneNumber)
     *     UserService-->>-AuthenticationService: Optional<User>
     *
     *     alt User not found or invalid password
     *         AuthenticationService-->>LoginController: ApiException (401 INVALID_CREDENTIALS)
     *         LoginController-->>-Client: 401 Unauthorized
     *     else User found and password valid
     *         AuthenticationService->>+OTPService: generateAndSendOtp(userId)
     *         OTPService-->>-AuthenticationService: (void)
     *
     *         alt OTP sending fails
     *             AuthenticationService-->>LoginController: ApiException (500 OTP_SEND_FAILURE)
     *             LoginController-->>-Client: 500 Internal Server Error
     *         else OTP sent successfully
     *             AuthenticationService->>+TokenService: generateOtpSessionToken(userId)
     *             TokenService-->>-AuthenticationService: otpSessionToken (JWT)
     *             AuthenticationService-->>-LoginController: LoginResponse
     *             LoginController-->>-Client: 200 OK (message, otpSessionToken)
     *         end
     *     end
     */

    /**
     * Orchestrates the user login process.
     * Finds the user, verifies credentials, handles account locking, and triggers OTP flow on success.
     * @param request The login request containing credentials.
     * @return A LoginResponse with a success message and session token.
     * @throws ApiException if authentication fails, the account is locked, or an internal error occurs.
     */
    @Override
    @Loggable
    public LoginResponse login(LoginRequest request) {
        Optional<User> userOptional = userService.findByPhoneNumber(request.getPhoneNumber());

        if (userOptional.isEmpty()) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, new ApiError(ErrorCode.INVALID_CREDENTIALS, "The phone number or password you entered is incorrect."));
        }

        User user = userOptional.get();

        if (user.isLocked() && user.getLockoutEndDate() != null && user.getLockoutEndDate().isAfter(LocalDateTime.now())) {
            throw new ApiException(HttpStatus.FORBIDDEN, new ApiError(ErrorCode.ACCOUNT_LOCKED, "Account is locked due to too many failed login attempts. Please try again later."));
        }

        if (!userService.verifyPassword(request.getPassword(), user.getPasswordHash())) {
            userService.handleFailedLogin(user);
            throw new ApiException(HttpStatus.UNAUTHORIZED, new ApiError(ErrorCode.INVALID_CREDENTIALS, "The phone number or password you entered is incorrect."));
        }

        userService.handleSuccessfulLogin(user);

        otpService.generateAndSendOtp(user.getPhoneNumber(), user.getId());
        String otpSessionToken = tokenService.generateOtpSessionToken(user.getId());

        return new LoginResponse("OTP has been sent to your mobile number.", otpSessionToken);
    }
}
