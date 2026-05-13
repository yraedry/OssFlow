package com.ossflow.catalog.ruleset.infrastructure.persistence;

import com.ossflow.catalog.technique.domain.Belt;
import com.ossflow.catalog.technique.domain.Modality;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RulesetJpaRepository extends JpaRepository<RulesetEntity, Long> {

    boolean existsByFederationIdAndBeltAndModalityAndEffectiveFrom(
            Long federationId, Belt belt, Modality modality, java.time.LocalDate effectiveFrom);

    @EntityGraph(attributePaths = "federation")
    Optional<RulesetEntity> findWithFederationById(Long id);

    @Query(value = """
            SELECT r FROM RulesetEntity r
            LEFT JOIN FETCH r.federation
            WHERE (:federationId IS NULL OR r.federationId = :federationId)
            AND (:belt IS NULL OR r.belt = :belt)
            AND (:modality IS NULL OR r.modality = :modality)
            """,
            countQuery = """
            SELECT COUNT(r) FROM RulesetEntity r
            WHERE (:federationId IS NULL OR r.federationId = :federationId)
            AND (:belt IS NULL OR r.belt = :belt)
            AND (:modality IS NULL OR r.modality = :modality)
            """)
    Page<RulesetEntity> findByFilters(Long federationId, Belt belt, Modality modality, Pageable pageable);
}
