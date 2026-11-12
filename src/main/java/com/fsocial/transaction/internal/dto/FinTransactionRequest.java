package com.fsocial.transaction.internal.dto;

import com.fsocial.transaction.internal.entity.FinTransactionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FinTransactionRequest {
    @NotBlank(message = "Tên giao dịch không được để trống")
    String name;
    String descriptions;
    String avatarUrl;

    @NotNull(message = "Số tiền không được để trống")
    @Positive(message = "Số tiền phải lớn hơn 0")
    BigDecimal amount;

    @NotNull(message = "Loại giao dịch không được để trống")
    FinTransactionType type;

    /** Tùy chọn: null/rỗng = giao dịch chưa phân loại. */
    String finProductId;
}
