package com.fsocial.termofservice.internal.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TermOfServiceDTO {
    String id;
    String name;
    boolean status = true;
}
