package com.fsocial.user.internal.rbac;

import com.fsocial.user.internal.rbac.PermissionRequest;
import com.fsocial.user.internal.rbac.PermissionResponse;
import com.fsocial.user.internal.rbac.Permission;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    Permission toPermission(PermissionRequest request);
    PermissionResponse toPermissionResponse(Permission permission);
}
