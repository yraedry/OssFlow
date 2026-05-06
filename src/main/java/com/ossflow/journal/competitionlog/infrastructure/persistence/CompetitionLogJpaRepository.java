package com.ossflow.journal.competitionlog.infrastructure.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompetitionLogJpaRepository extends JpaRepository<CompetitionLogEntity, Long> {

    @Query("SELECT cl FROM CompetitionLogEntity cl LEFT JOIN FETCH cl.matches WHERE cl.id = :id AND cl.ownerId = :ownerId")
    Optional<CompetitionLogEntity> findByIdAndOwnerId(Long id, Long ownerId);

    Page<CompetitionLogEntity> findByOwnerId(Long ownerId, Pageable pageable);
}
