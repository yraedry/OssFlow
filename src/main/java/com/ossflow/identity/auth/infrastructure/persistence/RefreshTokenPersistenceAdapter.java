package com.ossflow.identity.auth.infrastructure.persistence;

import com.ossflow.identity.auth.application.port.RefreshTokenRepositoryPort;
import com.ossflow.identity.auth.domain.RefreshToken;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;

@Component
public class RefreshTokenPersistenceAdapter implements RefreshTokenRepositoryPort {

    private final RefreshTokenJpaRepository jpaRepository;

    public RefreshTokenPersistenceAdapter(RefreshTokenJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public RefreshToken save(RefreshToken token) {
        RefreshTokenEntity entity = RefreshTokenEntity.builder()
                .id(token.id())
                .accountId(token.accountId())
                .tokenHash(token.tokenHash())
                .tokenVersion(token.tokenVersion())
                .expiresAt(token.expiresAt())
                .createdAt(token.createdAt())
                .revokedAt(token.revokedAt())
                .replacedById(token.replacedById())
                .build();
        RefreshTokenEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<RefreshToken> findByTokenHash(String tokenHash) {
        return jpaRepository.findByTokenHash(tokenHash).map(this::toDomain);
    }

    @Override
    public void revokeByAccountId(Long accountId) {
        jpaRepository.revokeAllByAccountId(accountId, Instant.now());
    }

    @Override
    public Optional<RefreshToken> findById(Long id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    private RefreshToken toDomain(RefreshTokenEntity e) {
        return new RefreshToken(e.getId(), e.getAccountId(), e.getTokenHash(),
                e.getTokenVersion(), e.getExpiresAt(), e.getCreatedAt(), e.getRevokedAt(),
                e.getReplacedById());
    }
}
