package com.fsocial.auth.internal.controller;

import com.fsocial.shared.response.ApiResponse;
import com.fsocial.auth.internal.dto.GoogleDTORequest;

import com.fsocial.user.internal.dto.AccountResponse;
import com.fsocial.auth.internal.dto.AuthenticationResponse;
import com.fsocial.user.internal.dto.DuplicationResponse;
import com.fsocial.auth.internal.dto.IntrospectResponse;
import com.fsocial.shared.exception.StatusCode;
import com.fsocial.user.internal.service.AccountService;
import com.fsocial.auth.internal.service.AuthenticationService;
import com.fsocial.auth.internal.service.OtpService;
import com.fsocial.auth.internal.service.RefreshTokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import com.fsocial.auth.internal.dto.AccountLoginRequest;
import com.fsocial.auth.internal.dto.EmailRequest;
import com.fsocial.auth.internal.dto.OtpRequest;
import com.fsocial.auth.internal.dto.RefreshTokenRequest;
import com.fsocial.auth.internal.dto.ResetPasswordRequest;
import com.fsocial.auth.internal.dto.TokenRequest;
import com.fsocial.user.internal.dto.AccountRegisterRequest;
import com.fsocial.user.internal.dto.DuplicationRequest;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping("/auth")
@Slf4j
public class AuthenticateController {

    AuthenticationService authenticationService;
    RefreshTokenService refreshTokenService;
    AccountService accountService;
    OtpService otpService;

    @PostMapping("/introspect")
    public ApiResponse<IntrospectResponse> introspect(@RequestBody @Valid TokenRequest token) {
        return ApiResponse.<IntrospectResponse>builder()
                .statusCode(StatusCode.SUCCESS.getCode())
                .message(StatusCode.SUCCESS.getMessage())
                .data(authenticationService.introspect(token.getToken()))
                .build();
    }

    @PostMapping("/login")
    public ApiResponse<AuthenticationResponse> handleLogin(@RequestBody @Valid AccountLoginRequest request,
            @RequestHeader("User-Agent") String userAgent,
            HttpServletRequest httpRequest) {
        return ApiResponse.<AuthenticationResponse>builder()
                .statusCode(StatusCode.SUCCESS.getCode())
                .message(StatusCode.SUCCESS.getMessage())
                .data(authenticationService.login(request, userAgent, httpRequest))
                .build();
    }

    @PostMapping("/refresh-token")
    public ApiResponse<AuthenticationResponse> refreshAccessToken(@RequestBody @Valid RefreshTokenRequest request,
            @RequestHeader("User-Agent") String userAgent,
            HttpServletRequest httpRequest) {
        return ApiResponse.<AuthenticationResponse>builder()
                .statusCode(StatusCode.SUCCESS.getCode())
                .message(StatusCode.SUCCESS.getMessage())
                .data(refreshTokenService.refreshAccessToken(request.getRefreshToken(), userAgent, httpRequest.getRemoteAddr()))
                .build();
    }

    @PostMapping("/logout")
    public ApiResponse<?> logout(@RequestBody @Valid TokenRequest refreshToken, HttpServletRequest httpRequest) {
        refreshTokenService.disableRefreshToken(refreshToken.getToken());
        SecurityContextHolder.clearContext();
        httpRequest.getSession().invalidate();
        return ApiResponse.builder()
                .statusCode(StatusCode.SUCCESS.getCode())
                .message(StatusCode.SUCCESS.getMessage())
                .build();
    }


    @PutMapping("/reset-password")
    public ApiResponse<Void> resetPassword(@RequestBody @Valid ResetPasswordRequest request) {
        accountService.resetPassword(request.getEmail(), request.getNewPassword());
        return ApiResponse.<Void>builder()
                .statusCode(StatusCode.PASSWORD_RESET_SUCCESS.getCode())
                .message(StatusCode.PASSWORD_RESET_SUCCESS.getMessage())
                .build();
    }

    @PostMapping("/register")
    public ApiResponse<AccountResponse> persistAccount(@RequestBody @Valid AccountRegisterRequest request) {
        return ApiResponse.<AccountResponse>builder()
                .statusCode(StatusCode.ACCOUNT_REGISTERED.getCode())
                .message(StatusCode.ACCOUNT_REGISTERED.getMessage())
                .data(accountService.persistAccount(request))
                .build();
    }
    @PostMapping("/send-otp")
    public ApiResponse<Void> sendOtp(@RequestBody @Valid EmailRequest request) {
        otpService.sortTypeForSendOtp(request);
        return ApiResponse.<Void>builder()
                .statusCode(StatusCode.OTP_SENT.getCode())
                .message(StatusCode.OTP_SENT.getMessage())
                .build();
    }

    @PostMapping("/verify-otp")
    public ApiResponse<Void> verifyOtp(@RequestBody @Valid OtpRequest request) {
        otpService.sortTypeForVerifyOtp(request);
        return ApiResponse.<Void>builder()
                .statusCode(StatusCode.OTP_VALID.getCode())
                .message(StatusCode.OTP_VALID.getMessage())
                .build();
    }

    @PostMapping("/check-duplication")
    public ResponseEntity<ApiResponse<DuplicationResponse>> checkDuplication(@RequestBody @Valid DuplicationRequest request) {
        ApiResponse<DuplicationResponse> response = accountService.checkDuplication(request);
        HttpStatus status = response.getStatusCode() != 200 ? HttpStatus.BAD_REQUEST : HttpStatus.OK;
        return ResponseEntity.status(status).body(response);
    }

    @PostMapping("/google/login")
    public ApiResponse<AuthenticationResponse> loginWithGoogle(
            @RequestBody @Valid GoogleDTORequest request,
            @RequestHeader("User-Agent") String userAgent,
            HttpServletRequest httpRequest
            ){

        return ApiResponse.<AuthenticationResponse>builder()
                .statusCode(StatusCode.SUCCESS.getCode())
                .message(StatusCode.SUCCESS.getMessage())
                .data(authenticationService.loginWithGoogle(request, userAgent, httpRequest))
                .build();
    }
}
