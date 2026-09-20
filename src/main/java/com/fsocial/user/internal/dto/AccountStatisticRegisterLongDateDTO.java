package com.fsocial.user.internal.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor(force = true)
@Builder
@Data
public class AccountStatisticRegisterLongDateDTO {
    Date date;
    Long count;
}
