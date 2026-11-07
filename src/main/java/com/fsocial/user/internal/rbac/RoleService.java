package com.fsocial.user.internal.rbac;

import com.fsocial.user.internal.rbac.RoleCreationRequest;
import com.fsocial.user.internal.rbac.RoleResponse;

import java.util.List;
import java.util.Set;

public interface RoleService {
    RoleResponse createRole(RoleCreationRequest request);
    List<RoleResponse> getAllRoles();
    RoleResponse updateRole(String roleId, Set<String> newPermissions);
    void deleteRole(String name);
}
