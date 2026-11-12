package com.fsocial.transaction.internal.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FinCategoryRequest {
    @NotBlank(message = "Tên hạng mục không được để trống")
    String name;
    String descriptions;
    String avatarUrl;
}
