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
    @NotBlank(message = "Tên hashtag không được để trống")
    @Size(max = 64, message = "Tên hashtag tối đa 64 ký tự")
    String name;
}
