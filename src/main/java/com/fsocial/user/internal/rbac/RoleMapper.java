package com.fsocial.user.internal.rbac;

import com.fsocial.user.internal.rbac.RoleCreationRequest;
import com.fsocial.user.internal.rbac.RoleResponse;
import com.fsocial.user.internal.rbac.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    @Mapping(target = "permissions", ignore = true)
    Role toRole(RoleCreationRequest request);

    RoleResponse toRoleResponse(Role role);
}
