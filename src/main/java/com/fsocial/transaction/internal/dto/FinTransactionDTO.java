package com.fsocial.transaction.internal.dto;

import com.fsocial.transaction.internal.entity.FinTransactionType;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FinTransactionDTO {
    String id;
    String name;
    String descriptions;
    String avatarUrl;
    BigDecimal amount;
    FinTransactionType type;
    Boolean isBlocked;
    String finProductId;
    String finProductName;
    String finCategoryId;
    String finCategoryName;
    LocalDateTime createdAt;
}
