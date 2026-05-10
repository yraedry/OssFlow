package com.ossflow.identity.auth.application.port;

import com.ossflow.identity.auth.domain.RefreshToken;

import java.util.Optional;

public interface RefreshTokenRepositoryPort {
    RefreshToken save(RefreshToken token);
    Optional<RefreshToken> findByTokenHash(String tokenHash);
    void revokeByAccountId(Long accountId);
}
