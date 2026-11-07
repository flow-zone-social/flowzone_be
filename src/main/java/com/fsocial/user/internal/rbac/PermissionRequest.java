package com.fsocial.user.internal.rbac;

import lombok.*;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class PermissionRequest {
    String name;
    String description;
}
