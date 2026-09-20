package com.fsocial.post.internal.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class ContentResponse {
        String text;
        List<MediaResponse> media;
        String html;
}
