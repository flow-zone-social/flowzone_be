package com.fsocial.auth.internal.service;

import com.fsocial.auth.internal.dto.AuthenticationResponse;
import com.fsocial.auth.internal.entity.RefreshToken;

public interface RefreshTokenService {
    RefreshToken createRefreshToken(String accountId, String userAgent, String ipAddress);
    RefreshToken validRefreshToken(String token, String userAgent, String ipAddress);
    AuthenticationResponse refreshAccessToken(String refreshToken, String userAgent, String ipAddress);
    void disableRefreshToken(String refreshToken);
}
