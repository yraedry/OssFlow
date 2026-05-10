package com.ossflow.identity.auth.infrastructure.persistence;

import com.ossflow.identity.auth.application.port.PasswordResetTokenRepositoryPort;
import com.ossflow.identity.auth.domain.PasswordResetToken;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class PasswordResetTokenPersistenceAdapter implements PasswordResetTokenRepositoryPort {

    private final PasswordResetTokenJpaRepository jpaRepository;

    public PasswordResetTokenPersistenceAdapter(PasswordResetTokenJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public PasswordResetToken save(PasswordResetToken token) {
        PasswordResetTokenEntity entity = PasswordResetTokenEntity.builder()
                .id(token.id())
                .accountId(token.accountId())
                .tokenHash(token.tokenHash())
                .expiresAt(token.expiresAt())
                .usedAt(token.usedAt())
                .build();
        PasswordResetTokenEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<PasswordResetToken> findByTokenHash(String tokenHash) {
        return jpaRepository.findByTokenHash(tokenHash).map(this::toDomain);
    }

    private PasswordResetToken toDomain(PasswordResetTokenEntity e) {
        return new PasswordResetToken(e.getId(), e.getAccountId(), e.getTokenHash(),
                e.getExpiresAt(), e.getUsedAt());
    }
}
