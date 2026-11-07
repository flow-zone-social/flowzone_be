package com.fsocial.post.api;

import com.fsocial.media.api.MediaItemDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContentDTO {
    String text;
    String html;
    List<MediaItemDTO> media;
}
