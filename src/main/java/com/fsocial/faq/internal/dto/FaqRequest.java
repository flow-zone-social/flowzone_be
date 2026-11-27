package com.fsocial.faq.internal.dto;

import com.fsocial.faq.internal.enums.FaqType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FaqRequest {
    @NotBlank(message = "Name is required")
    String name;

    String description;

    @NotBlank(message = "Content is required")
    String content;

    @NotNull(message = "Type is required")
    FaqType type;

    String attachmentId;
}
