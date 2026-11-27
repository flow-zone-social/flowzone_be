package com.fsocial.transaction.internal.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FinWalletDTO {
    String id;
    BigDecimal balance;
    LocalDateTime updatedAt;
}
