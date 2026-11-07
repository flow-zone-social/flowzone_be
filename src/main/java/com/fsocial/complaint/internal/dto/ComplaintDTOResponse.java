package com.fsocial.complaint.internal.dto;

import com.fsocial.complaint.internal.enums.ComplaintType;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ComplaintDTOResponse {
    String id;
    String targetId;
    String userId;
    String displayName;
    String profileId;
    ComplaintType complaintType;
    String termOfService;
    LocalDateTime createDatetime;
    boolean isRead;
    int reportCount;
}

