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
    @NotBlank(message = "Category name is required")
    String name;
    String descriptions;
    String avatarUrl;
}
