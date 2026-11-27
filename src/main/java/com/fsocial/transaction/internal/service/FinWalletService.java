package com.fsocial.transaction.internal.service;

import com.fsocial.transaction.internal.dto.FinWalletDTO;
import com.fsocial.transaction.internal.entity.FinTransactionType;

import java.math.BigDecimal;

public interface FinWalletService {

    /** Ví của user; chưa có giao dịch nào thì trả ví số dư 0 (không tạo bản ghi). */
    FinWalletDTO getMyWallet(String userId);

    /** Ghi nhận giao dịch mới: INCOME cộng, EXPENSE trừ. Tự tạo ví nếu user chưa có. */
    void record(String userId, FinTransactionType type, BigDecimal amount);

    /** Hoàn tác giao dịch đã ghi (xóa / sửa giao dịch): INCOME trừ lại, EXPENSE cộng lại. */
    void undo(String userId, FinTransactionType type, BigDecimal amount);

    void create (String userId);
}
