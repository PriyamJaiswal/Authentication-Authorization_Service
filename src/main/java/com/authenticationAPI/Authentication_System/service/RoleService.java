package com.authenticationAPI.Authentication_System.service;

import com.authenticationAPI.Authentication_System.model.Permission;
import com.authenticationAPI.Authentication_System.model.Role;
import com.authenticationAPI.Authentication_System.repo.PermissionRepository;
import com.authenticationAPI.Authentication_System.repo.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    public Role getRoleById(Long id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found with id: " + id));
    }

    public Role getRoleByName(String name) {
        return roleRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Role not found with name: " + name));
    }

    @Transactional
    public Role createRole(Role role) {
        if (roleRepository.existsByName(role.getName())) {
            throw new RuntimeException("Role already exists");
        }

        Role savedRole = roleRepository.save(role);
        log.info("Role created: {}", savedRole.getName());

        return savedRole;
    }

    @Transactional
    public Role updateRole(Long id, Role roleDetails) {
        Role role = getRoleById(id);

        if (roleDetails.getName() != null && !roleDetails.getName().equals(role.getName())) {
            if (roleRepository.existsByName(roleDetails.getName())) {
                throw new RuntimeException("Role name already exists");
            }
            role.setName(roleDetails.getName());
        }

        Role updatedRole = roleRepository.save(role);
        log.info("Role updated: {}", updatedRole.getName());

        return updatedRole;
    }

    @Transactional
    public void deleteRole(Long id) {
        Role role = getRoleById(id);
        roleRepository.delete(role);
        log.info("Role deleted: {}", role.getName());
    }

    @Transactional
    public Role assignPermissionsToRole(Long roleId, Set<String> permissionNames) {
        Role role = getRoleById(roleId);

        Set<Permission> permissions = role.getPermissions();
        for (String permissionName : permissionNames) {
            Permission permission = permissionRepository.findByName(permissionName)
                    .orElseThrow(() -> new RuntimeException("Permission not found: " + permissionName));
            permissions.add(permission);
        }

        role.setPermissions(permissions);
        Role updatedRole = roleRepository.save(role);

        log.info("Permissions assigned to role: {}", role.getName());
        return updatedRole;
    }

    @Transactional
    public Role removePermissionsFromRole(Long roleId, Set<String> permissionNames) {
        Role role = getRoleById(roleId);

        Set<Permission> permissions = role.getPermissions();
        for (String permissionName : permissionNames) {
            permissions.removeIf(permission -> permission.getName().equals(permissionName));
        }

        role.setPermissions(permissions);
        Role updatedRole = roleRepository.save(role);

        log.info("Permissions removed from role: {}", role.getName());
        return updatedRole;
    }
}