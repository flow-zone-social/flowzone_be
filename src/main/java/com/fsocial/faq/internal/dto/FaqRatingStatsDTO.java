package com.fsocial.faq.internal.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FaqRatingStatsDTO {
    Double averageRating;
    Long ratingCount;
}
