package com.ossflow.identity.auth.application.port;

import com.ossflow.identity.auth.domain.PasswordResetToken;

import java.util.Optional;

public interface PasswordResetTokenRepositoryPort {
    PasswordResetToken save(PasswordResetToken token);
    Optional<PasswordResetToken> findByTokenHash(String tokenHash);
}
