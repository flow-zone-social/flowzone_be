package com.fsocial.post.internal.feed.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class HashtagRequest {
    @NotBlank(message = "Hashtag name is required")
    @Size(max = 64, message = "Hashtag name must be at most 64 characters")
    String name;
}
