package com.fsocial.transaction.internal.entity;

import com.fsocial.shared.exception.AppException;
import com.fsocial.shared.exception.StatusCode;
import com.fsocial.shared.persistence.AbstractEntity;
import com.fsocial.user.internal.entity.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "fin_wallets")
public class FinWallet extends AbstractEntity<String> {
    @Column(name = "balance")
    @Builder.Default
    BigDecimal balance = BigDecimal.ZERO;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", unique = true)
    User user;

    @Version
    Long version;

    public void income(BigDecimal amount){
        requirePositive(amount);
        balance = balance.add(amount);
    }

    public void expense(BigDecimal amount) {
        requirePositive(amount);
        balance = balance.subtract(amount);
    }

//    Create new transaction
    public void record (FinTransactionType type, BigDecimal amount){
        switch (type) {
            case INCOME -> income(amount);
            case EXPENSE -> expense(amount);
        }
    }

//    Hoàn tác giao dịch (sửa/xóa): ngược lại với record
    public void undo(FinTransactionType type, BigDecimal amount){
        switch (type){
            case INCOME -> expense(amount);
            case EXPENSE -> income(amount);
        }
    }

    public static void requirePositive(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new AppException(StatusCode.FIN_AMOUNT_CANNOT_BE_NEGATIVE);
        }
    }
}
