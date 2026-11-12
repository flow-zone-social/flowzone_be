package com.fsocial.transaction.internal.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FinProductRequest {
    @NotBlank(message = "Tên phân loại không được để trống")
    String name;
    String descriptions;
    String avatarUrl;

    /** Tùy chọn: null/rỗng = product không thuộc hạng mục nào. */
    String finCategoryId;
}
