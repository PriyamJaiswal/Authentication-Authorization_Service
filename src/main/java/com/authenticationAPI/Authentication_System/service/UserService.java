package com.authenticationAPI.Authentication_System.service;

import com.authenticationAPI.Authentication_System.model.Role;
import com.authenticationAPI.Authentication_System.model.User;
import com.authenticationAPI.Authentication_System.repo.RoleRepository;
import com.authenticationAPI.Authentication_System.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));
    }

    @Transactional
    public User createUser(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);

        log.info("User created: {}", savedUser.getUsername());
        return savedUser;
    }

    @Transactional
    public User updateUser(UUID id, User userDetails) {
        User user = getUserById(id);

        if (userDetails.getUsername() != null && !userDetails.getUsername().equals(user.getUsername())) {
            if (userRepository.existsByUsername(userDetails.getUsername())) {
                throw new RuntimeException("Username already exists");
            }
            user.setUsername(userDetails.getUsername());
        }

        if (userDetails.getEmail() != null && !userDetails.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(userDetails.getEmail())) {
                throw new RuntimeException("Email already exists");
            }
            user.setEmail(userDetails.getEmail());
        }

        if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDetails.getPassword()));
        }

        if (userDetails.getEnabled() != null) {
            user.setEnabled(userDetails.getEnabled());
        }

        User updatedUser = userRepository.save(user);
        log.info("User updated: {}", updatedUser.getUsername());

        return updatedUser;
    }

    @Transactional
    public void deleteUser(UUID id) {
        User user = getUserById(id);
        userRepository.delete(user);
        log.info("User deleted: {}", user.getUsername());
    }

    @Transactional
    public User assignRolesToUser(UUID userId, Set<String> roleNames) {
        User user = getUserById(userId);

        Set<Role> roles = user.getRoles();
        for (String roleName : roleNames) {
            Role role = roleRepository.findByName(roleName)
                    .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));
            roles.add(role);
        }

        user.setRoles(roles);
        User updatedUser = userRepository.save(user);

        log.info("Roles assigned to user: {}", user.getUsername());
        return updatedUser;
    }

    @Transactional
    public User removeRolesFromUser(UUID userId, Set<String> roleNames) {
        User user = getUserById(userId);

        Set<Role> roles = user.getRoles();
        for (String roleName : roleNames) {
            roles.removeIf(role -> role.getName().equals(roleName));
        }

        user.setRoles(roles);
        User updatedUser = userRepository.save(user);

        log.info("Roles removed from user: {}", user.getUsername());
        return updatedUser;
    }

    @Transactional
    public User enableUser(UUID id) {
        User user = getUserById(id);
        user.setEnabled(true);
        return userRepository.save(user);
    }

    @Transactional
    public User disableUser(UUID id) {
        User user = getUserById(id);
        user.setEnabled(false);
        return userRepository.save(user);
    }
}