package com.fsocial.user.internal.rbac;

import com.fsocial.user.internal.rbac.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, String> {
}
