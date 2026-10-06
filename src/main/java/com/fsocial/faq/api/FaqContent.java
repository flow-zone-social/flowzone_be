package com.fsocial.faq.api;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FaqContent {
    String name;
    String description;
    String content;
    String type;
}
