package com.fsocial.transaction.internal.service;

import com.fsocial.shared.exception.AppException;
import com.fsocial.shared.exception.StatusCode;
import com.fsocial.transaction.api.FinWalletApi;
import com.fsocial.transaction.internal.dto.FinWalletDTO;
import com.fsocial.transaction.internal.entity.FinTransactionType;
import com.fsocial.transaction.internal.entity.FinWallet;
import com.fsocial.transaction.internal.mapper.FinMapper;
import com.fsocial.transaction.internal.repository.FinWalletRepository;
import com.fsocial.user.internal.entity.User;
import com.fsocial.user.internal.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FinWalletServiceImpl implements FinWalletService, FinWalletApi {

    FinWalletRepository finWalletRepository;
    UserRepository userRepository;
    FinMapper finMapper;

    @Override
    @Transactional(readOnly = true)
    public FinWalletDTO getMyWallet(String userId) {
        return finWalletRepository.findByUserId(userId)
                .map(finMapper::toDTO)
                .orElseGet(() -> FinWalletDTO.builder().balance(BigDecimal.ZERO).build());
    }

    @Override
    @Transactional
    public void record(String userId, FinTransactionType type, BigDecimal amount) {
        FinWallet wallet = lockOrCreate(userId);
        wallet.record(type, amount);
        finWalletRepository.save(wallet);
    }

    @Override
    @Transactional
    public void undo(String userId, FinTransactionType type, BigDecimal amount) {
        FinWallet wallet = lockOrCreate(userId);
        wallet.undo(type, amount);
        finWalletRepository.save(wallet);
    }

    @Override
    public void create(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(StatusCode.USER_NOT_FOUND));
        finWalletRepository.save(FinWallet.builder()
                .user(user)
                .build());
    }

    /**
     * Khóa ví để cộng/trừ tuần tự; user chưa có ví thì tạo mới (số dư 0).
     */
    private FinWallet lockOrCreate(String userId) {
        return finWalletRepository.findByUserIdForUpdate(userId)
                .orElseThrow(() -> new AppException(StatusCode.FIN_WALLET_NOT_FOUND));
    }
}
