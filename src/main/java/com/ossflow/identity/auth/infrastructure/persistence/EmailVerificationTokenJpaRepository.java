package com.ossflow.identity.auth.infrastructure.persistence;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.util.Optional;

public interface EmailVerificationTokenJpaRepository extends JpaRepository<EmailVerificationTokenEntity, Long> {
    Optional<EmailVerificationTokenEntity> findByTokenHash(String tokenHash);

    @Modifying
    @Transactional
    void deleteByAccountId(Long accountId);
}
