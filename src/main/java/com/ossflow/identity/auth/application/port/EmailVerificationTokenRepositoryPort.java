package com.ossflow.identity.auth.application.port;

import com.ossflow.identity.auth.domain.EmailVerificationToken;

import java.util.Optional;

public interface EmailVerificationTokenRepositoryPort {
    EmailVerificationToken save(EmailVerificationToken token);
    Optional<EmailVerificationToken> findByTokenHash(String tokenHash);
    void deleteByAccountId(Long accountId);
}
