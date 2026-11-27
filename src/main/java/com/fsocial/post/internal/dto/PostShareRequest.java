package com.fsocial.post.internal.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PostShareRequest {
    @NotBlank(message = "User ID is required")
    String userId;
    String text;
    String html;
    @NotBlank(message = "originPost is required")
    String originPostId;
}
