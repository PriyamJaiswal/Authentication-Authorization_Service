package com.authenticationAPI.Authentication_System.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.UUID;

@Document(collection = "tokens")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenDocument {

    @Id
    private String id;

    private UUID userId;

    private String token;

    private LocalDateTime issuedAt;

    private LocalDateTime expiresAt;

    private Boolean revoked = false;

    private String ipAddress;

    private String userAgent;
}