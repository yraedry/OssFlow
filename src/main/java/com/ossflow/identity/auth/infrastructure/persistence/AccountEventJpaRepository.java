package com.ossflow.identity.auth.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountEventJpaRepository extends JpaRepository<AccountEventEntity, Long> {
}
