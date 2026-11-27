package com.fsocial.complaint.internal.dto;

import com.fsocial.complaint.internal.enums.ComplaintType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ComplaintDTO {
    @NotBlank(message = "targetId must not be blank")
    String targetId;
    @NotNull(message = "complaintType must not be null")
    ComplaintType complaintType;
    @NotBlank(message = "termOfServiceId must not be blank")
    String termOfServiceId;
}
