package com.fsocial.transaction.internal.entity;

import com.fsocial.shared.persistence.AbstractEntity;
import com.fsocial.user.internal.entity.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

/**
 * Số dư của 1 user. Chỉ là bản lưu sẵn của (tổng INCOME - tổng EXPENSE) trong fin_transactions.
 * Không sửa balance bằng setter rồi save(): cập nhật phải đi qua câu SQL cộng dồn nguyên tử
 * để hai giao dịch đồng thời không ghi đè nhau.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "fin_wallets")
public class FinWallet extends AbstractEntity<String> {
    @Column(name = "balance")
    BigDecimal balance = BigDecimal.ZERO;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", unique = true)
    User user;
}
