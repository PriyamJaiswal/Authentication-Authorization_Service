package com.authenticationAPI.Authentication_System.service;

import com.authenticationAPI.Authentication_System.model.Role;
import com.authenticationAPI.Authentication_System.model.TokenDocument;
import com.authenticationAPI.Authentication_System.model.User;
import com.authenticationAPI.Authentication_System.repo.RoleRepository;
import com.authenticationAPI.Authentication_System.repo.TokenRepository;
import com.authenticationAPI.Authentication_System.repo.UserRepository;
import com.authenticationAPI.Authentication_System.securityComponent.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    @Transactional
    public User register(User user, HttpServletRequest request) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Assign default USER role
        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new RuntimeException("Default role not found"));

        Set<Role> roles = new HashSet<>();
        roles.add(userRole);
        user.setRoles(roles);

        User savedUser = userRepository.save(user);
        log.info("New user registered: {}", savedUser.getUsername());

        return savedUser;
    }

    @Transactional
    public String login(String username, String password, HttpServletRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = tokenProvider.generateToken(authentication);

        // Save token to MongoDB
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        TokenDocument tokenDocument = new TokenDocument();
        tokenDocument.setUserId(user.getId());
        tokenDocument.setToken(jwt);
        tokenDocument.setIssuedAt(LocalDateTime.now());
        tokenDocument.setExpiresAt(
                tokenProvider.getExpirationDateFromToken(jwt)
                        .toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime()
        );
        tokenDocument.setRevoked(false);
        tokenDocument.setIpAddress(getClientIP(request));
        tokenDocument.setUserAgent(request.getHeader("User-Agent"));

        tokenRepository.save(tokenDocument);

        log.info("User logged in: {}", username);
        return jwt;
    }

    @Transactional
    public void logout(String token) {
        TokenDocument tokenDocument = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token not found"));

        tokenDocument.setRevoked(true);
        tokenRepository.save(tokenDocument);

        SecurityContextHolder.clearContext();
        log.info("User logged out");
    }

    @Transactional
    public void revokeAllUserTokens(UUID userId) {
        tokenRepository.findByUserIdAndRevokedFalse(userId)
                .forEach(token -> {
                    token.setRevoked(true);
                    tokenRepository.save(token);
                });

        log.info("All tokens revoked for user: {}", userId);
    }

    public boolean isTokenRevoked(String token) {
        return tokenRepository.findByToken(token)
                .map(TokenDocument::getRevoked)
                .orElse(true);
    }

    private String getClientIP(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }
}