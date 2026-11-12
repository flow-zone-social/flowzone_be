package com.fsocial.transaction.internal.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FinCategoryDTO {
    String id;
    String name;
    String descriptions;
    String avatarUrl;
}
