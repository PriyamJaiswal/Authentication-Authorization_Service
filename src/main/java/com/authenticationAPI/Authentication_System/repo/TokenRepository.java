package com.authenticationAPI.Authentication_System.repo;

import com.authenticationAPI.Authentication_System.model.TokenDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TokenRepository extends MongoRepository<TokenDocument, String> {
    Optional<TokenDocument> findByToken(String token);
    List<TokenDocument> findByUserId(UUID userId);
    void deleteByToken(String token);
    List<TokenDocument> findByUserIdAndRevokedFalse(UUID userId);
}