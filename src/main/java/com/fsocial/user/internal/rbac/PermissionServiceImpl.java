package com.fsocial.user.internal.rbac;

import com.fsocial.user.internal.rbac.PermissionRequest;
import com.fsocial.user.internal.rbac.PermissionResponse;
import com.fsocial.user.internal.rbac.Permission;
import com.fsocial.user.internal.rbac.PermissionMapper;
import com.fsocial.user.internal.rbac.PermissionRepository;
import com.fsocial.user.internal.rbac.PermissionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PermissionServiceImpl implements PermissionService {

    PermissionRepository permissionRepository;
    PermissionMapper permissionMapper;

    @Override
    public PermissionResponse createPermission(PermissionRequest request) {
        Permission permission = permissionMapper.toPermission(request);
        return permissionMapper.toPermissionResponse(permissionRepository.save(permission));
    }

    @Override
    public List<PermissionResponse> getAllPermissions() {
        return permissionRepository.findAll().stream()
                .map(permissionMapper::toPermissionResponse)
                .toList();
    }

    @Override
    public void deletePermission(String name) {
        permissionRepository.deleteById(name);
    }
}
