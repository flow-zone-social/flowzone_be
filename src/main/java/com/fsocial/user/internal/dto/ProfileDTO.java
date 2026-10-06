package com.fsocial.user.internal.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileDTO {
    String id;
    String firstName;
    String lastName;
    String avatar;
    String background;
    String bio;
    int gender;
}
