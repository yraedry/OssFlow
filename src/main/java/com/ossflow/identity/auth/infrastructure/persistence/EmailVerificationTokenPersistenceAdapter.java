package com.ossflow.identity.auth.infrastructure.persistence;

import com.ossflow.identity.auth.application.port.EmailVerificationTokenRepositoryPort;
import com.ossflow.identity.auth.domain.EmailVerificationToken;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class EmailVerificationTokenPersistenceAdapter implements EmailVerificationTokenRepositoryPort {

    private final EmailVerificationTokenJpaRepository jpaRepository;

    public EmailVerificationTokenPersistenceAdapter(EmailVerificationTokenJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public EmailVerificationToken save(EmailVerificationToken token) {
        EmailVerificationTokenEntity entity = EmailVerificationTokenEntity.builder()
                .id(token.id())
                .accountId(token.accountId())
                .tokenHash(token.tokenHash())
                .expiresAt(token.expiresAt())
                .usedAt(token.usedAt())
                .build();
        EmailVerificationTokenEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<EmailVerificationToken> findByTokenHash(String tokenHash) {
        return jpaRepository.findByTokenHash(tokenHash).map(this::toDomain);
    }

    @Override
    public void deleteByAccountId(Long accountId) {
        jpaRepository.deleteByAccountId(accountId);
    }

    private EmailVerificationToken toDomain(EmailVerificationTokenEntity e) {
        return new EmailVerificationToken(e.getId(), e.getAccountId(), e.getTokenHash(),
                e.getExpiresAt(), e.getUsedAt());
    }
}
