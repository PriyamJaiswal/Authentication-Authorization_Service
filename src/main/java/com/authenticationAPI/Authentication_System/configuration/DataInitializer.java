package com.authenticationAPI.Authentication_System.configuration;

import com.authenticationAPI.Authentication_System.model.Permission;
import com.authenticationAPI.Authentication_System.model.Role;
import com.authenticationAPI.Authentication_System.model.User;
import com.authenticationAPI.Authentication_System.repo.PermissionRepository;
import com.authenticationAPI.Authentication_System.repo.RoleRepository;
import com.authenticationAPI.Authentication_System.repo.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        initializePermissions();
        initializeRoles();
        initializeAdminUser();
    }

    private void initializePermissions() {
        String[] permissionNames = {
                "READ_USER", "CREATE_USER", "UPDATE_USER", "DELETE_USER",
                "MANAGE_ROLES", "MANAGE_PERMISSIONS",
                "READ_ADMIN", "WRITE_ADMIN", "DELETE_ADMIN"
        };

        for (String permissionName : permissionNames) {
            if (!permissionRepository.existsByName(permissionName)) {
                Permission permission = new Permission(permissionName);
                permissionRepository.save(permission);
                log.info("Created permission: {}", permissionName);
            }
        }
    }

    private void initializeRoles() {
        // Create ADMIN role with all permissions
        if (!roleRepository.existsByName("ADMIN")) {
            Role adminRole = new Role("ADMIN");

            // Fetch permissions from database to ensure they're managed entities
            Set<Permission> adminPermissions = new HashSet<>();
            permissionRepository.findAll().forEach(adminPermissions::add);

            adminRole.setPermissions(adminPermissions);
            roleRepository.save(adminRole);
            log.info("Created ADMIN role with all permissions");
        }

        // Create USER role with basic permissions
        if (!roleRepository.existsByName("USER")) {
            Role userRole = new Role("USER");
            Set<Permission> userPermissions = new HashSet<>();

            // Fetch permission from database
            permissionRepository.findByName("READ_USER")
                    .ifPresent(userPermissions::add);

            userRole.setPermissions(userPermissions);
            roleRepository.save(userRole);
            log.info("Created USER role with basic permissions");
        }

        // Create MODERATOR role
        if (!roleRepository.existsByName("MODERATOR")) {
            Role moderatorRole = new Role("MODERATOR");
            Set<Permission> moderatorPermissions = new HashSet<>();

            // Fetch permissions from database
            permissionRepository.findByName("READ_USER")
                    .ifPresent(moderatorPermissions::add);
            permissionRepository.findByName("UPDATE_USER")
                    .ifPresent(moderatorPermissions::add);

            moderatorRole.setPermissions(moderatorPermissions);
            roleRepository.save(moderatorRole);
            log.info("Created MODERATOR role");
        }
    }

    private void initializeAdminUser() {
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@example.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setEnabled(true);

            // Fetch role from database to ensure it's a managed entity
            Role adminRole = roleRepository.findByName("ADMIN")
                    .orElseThrow(() -> new RuntimeException("ADMIN role not found"));

            Set<Role> roles = new HashSet<>();
            roles.add(adminRole);
            admin.setRoles(roles);

            userRepository.save(admin);
            log.info("Created default admin user - username: admin, password: admin123");
        }
    }
}