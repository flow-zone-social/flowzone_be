package com.fsocial.complaint.internal.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ComplaintStatisticsLongDayDTO {
    LocalDateTime date;
    int count;
}

