package com.ossflow.identity.auth.infrastructure.persistence;

import com.ossflow.identity.auth.domain.AccountProvider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountJpaRepository extends JpaRepository<AccountEntity, Long> {
    Optional<AccountEntity> findByEmail(String email);
    Optional<AccountEntity> findByProviderAndProviderId(AccountProvider provider, String providerId);
}
