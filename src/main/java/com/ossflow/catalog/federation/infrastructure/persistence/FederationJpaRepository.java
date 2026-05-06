package com.ossflow.catalog.federation.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FederationJpaRepository extends JpaRepository<FederationEntity, Long> {
    Optional<FederationEntity> findByCode(String code);
    boolean existsByCode(String code);
}
