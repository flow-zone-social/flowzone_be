package com.fsocial.user.internal.rbac;

import com.fsocial.shared.response.ApiResponse;
import com.fsocial.user.internal.rbac.PermissionRequest;
import com.fsocial.user.internal.rbac.PermissionResponse;
import com.fsocial.user.internal.rbac.PermissionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/permission")
public class PermissionController {

    PermissionService permissionService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ApiResponse<PermissionResponse> createPermission(@RequestBody PermissionRequest request) {
        return ApiResponse.<PermissionResponse>builder()
                .message("Create new Permission success.")
                .data(permissionService.createPermission(request))
                .build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ApiResponse<List<PermissionResponse>> getAllPermission() {
        return ApiResponse.<List<PermissionResponse>>builder()
                .message("Get all permission success.")
                .data(permissionService.getAllPermissions())
                .build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{permissionId}")
    public ApiResponse<Void> deletePermission(@PathVariable String permissionId) {
        permissionService.deletePermission(permissionId);
        return ApiResponse.<Void>builder()
                .message("Delete permission success.")
                .build();
    }
}
