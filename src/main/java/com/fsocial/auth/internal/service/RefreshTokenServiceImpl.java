package com.fsocial.auth.internal.service;

import com.fsocial.auth.internal.dto.AuthenticationResponse;
import com.fsocial.auth.internal.entity.RefreshToken;
import com.fsocial.shared.exception.StatusCode;
import com.fsocial.shared.exception.AppException;
import com.fsocial.user.internal.repository.AccountRepository;
import com.fsocial.auth.internal.repository.RefreshTokenRepository;
import com.fsocial.auth.internal.service.JwtService;
import com.fsocial.auth.internal.service.RefreshTokenService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class RefreshTokenServiceImpl implements RefreshTokenService {

    RefreshTokenRepository refreshTokenRepository;
    AccountRepository accountRepository;
    JwtService jwtService;

    @NonFinal
    @Value("${jwt.expired-time}")
    long expirationTime;

    @Override
    @Transactional
    public RefreshToken createRefreshToken(String accountId, String userAgent, String ipAddress) {
        accountRepository.findById(accountId)
                .orElseThrow(() -> new AppException(StatusCode.ACCOUNT_NOT_EXISTED));

        long tokenCount = refreshTokenRepository.countByAccountId(accountId);
        int MAX_REFRESH_TOKENS = 5;
        if (tokenCount >= MAX_REFRESH_TOKENS) {
            refreshTokenRepository.findFirstByAccountIdOrderByExpiryDateAsc(accountId)
                    .ifPresent(refreshTokenRepository::delete);
        }

        RefreshToken refreshToken = RefreshToken.builder()
                .token(generateRefreshToken())
                .accountId(accountId)
                .userAgent(userAgent)
                .ipAddress(ipAddress)
                .expiryDate(Instant.now().plus(expirationTime, ChronoUnit.DAYS))
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    @Override
    @Transactional
    public RefreshToken validRefreshToken(String token, String userAgent, String ipAddress) {
        RefreshToken existedRT = getRefreshToken(token);

        if (existedRT.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(existedRT);
            log.warn("Refresh token đã hết hạn: {}", token);
            throw new AppException(StatusCode.TOKEN_EXPIRED);
        }
        if (!existedRT.getUserAgent().equals(userAgent)) throw new AppException(StatusCode.UNAUTHENTICATED);
        if (!existedRT.getIpAddress().equals(ipAddress)) throw new AppException(StatusCode.UNAUTHENTICATED);

        return existedRT;
    }

    @Override
    public AuthenticationResponse refreshAccessToken(String refreshToken, String userAgent, String ipAddress) {
        RefreshToken token = validRefreshToken(refreshToken, userAgent, ipAddress);

        if (Duration.between(Instant.now(), token.getExpiryDate()).toHours() < 24) {
            log.info("Gia hạn RefreshToken thành công.");
            token.setExpiryDate(Instant.now().plus(expirationTime, ChronoUnit.DAYS));
            refreshTokenRepository.save(token);
        }

        return AuthenticationResponse.builder()
                .accessToken(jwtService.generateToken(token.getAccountId()))
                .refreshToken(token.getToken())
                .build();
    }

    @Override
    @Transactional
    public void disableRefreshToken(String refreshToken) {
        RefreshToken token = getRefreshToken(refreshToken);
        refreshTokenRepository.deleteByToken(token.getToken());
    }

    private RefreshToken getRefreshToken(String refreshToken) {
        return refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> {
                    log.warn("Refresh token không hợp lệ: {}", refreshToken);
                    return new AppException(StatusCode.INVALID_TOKEN);
                });
    }

    private String generateRefreshToken() {
        try {
            return Base64.getUrlEncoder().withoutPadding()
                    .encodeToString(SecureRandom.getInstanceStrong().generateSeed(32));
        } catch (NoSuchAlgorithmException e) {
            log.error("Lỗi tạo Refresh Token: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
