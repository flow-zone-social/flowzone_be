package com.fsocial.transaction.internal.dto;

import com.fsocial.transaction.internal.entity.FinTransactionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FinTransactionRequest {
    @NotBlank(message = "Transaction name is required")
    String name;
    String descriptions;
    String avatarUrl;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be greater than 0")
    BigDecimal amount;

    @NotNull(message = "Transaction type is required")
    FinTransactionType type;

    String finProductId;

    MultipartFile icon;
}
