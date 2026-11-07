package com.fsocial.user.internal.rbac;

import com.fsocial.user.internal.rbac.PermissionRequest;
import com.fsocial.user.internal.rbac.PermissionResponse;

import java.util.List;

public interface PermissionService {
    PermissionResponse createPermission(PermissionRequest request);
    List<PermissionResponse> getAllPermissions();
    void deletePermission(String name);
}
