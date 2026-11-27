package com.fsocial.transaction.internal.controller;

import com.fsocial.shared.response.ApiResponse;
import com.fsocial.transaction.internal.dto.FinWalletDTO;
import com.fsocial.transaction.internal.service.FinWalletService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/fin/wallet")
@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
public class FinWalletController {

    FinWalletService finWalletService;

    @GetMapping
    public ApiResponse<FinWalletDTO> getMyWallet(@AuthenticationPrincipal Jwt jwt) {
        return ApiResponse.<FinWalletDTO>builder()
                .message("Wallet retrieved successfully")
                .data(finWalletService.getMyWallet(jwt.getSubject()))
                .build();
    }
}
