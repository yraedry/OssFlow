package com.ossflow.identity.auth.infrastructure.persistence;

import com.ossflow.identity.auth.domain.EmailOutboxStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface EmailOutboxJpaRepository extends JpaRepository<EmailOutboxEntity, Long> {

    @Query("""
           SELECT e FROM EmailOutboxEntity e
           WHERE e.status IN (:pending, :failed)
             AND e.attempts < :maxAttempts
             AND (e.lastAttemptAt IS NULL OR e.lastAttemptAt < :retryBefore)
           ORDER BY e.id ASC
           """)
    List<EmailOutboxEntity> findRetriable(@Param("pending") EmailOutboxStatus pending,
                                          @Param("failed") EmailOutboxStatus failed,
                                          @Param("maxAttempts") int maxAttempts,
                                          @Param("retryBefore") Instant retryBefore,
                                          Pageable pageable);
}
