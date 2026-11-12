package com.fsocial.transaction.internal.entity;

import com.fsocial.shared.persistence.AbstractEntity;
import com.fsocial.user.internal.entity.User;
import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "fin_transactions")
public class FinTransaction extends AbstractEntity<String> {
    @Column(name = "name")
    String name;
    @Column(name = "descriptions")
    String descriptions;
    @Column(name = "avatar_url")
    String avatarUrl;
    @Column(name = "amount")
    BigDecimal amount;
    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    FinTransactionType type;
    @Column(name = "is_blocked")
    Boolean isBlocked = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fin_product_id")
    FinProduct finProduct;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    User user;
}
