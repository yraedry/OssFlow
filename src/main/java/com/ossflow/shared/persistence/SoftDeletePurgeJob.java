package com.ossflow.shared.persistence;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Slf4j
@Component
public class SoftDeletePurgeJob {

    @PersistenceContext private EntityManager em;

    private static final List<String> TABLES = List.of(
            "position", "technique", "system",
            "note", "training_session", "competition_log",
            "study_plan",
            "user_profile"
    );

    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void purgeExpired() {
        Instant now = Instant.now();
        for (String table : TABLES) {
            int deleted = em.createNativeQuery(
                    "DELETE FROM " + table + " WHERE purge_at IS NOT NULL AND purge_at < ?1")
                    .setParameter(1, now)
                    .executeUpdate();
            if (deleted > 0) {
                log.info("Purga {}: {} registros eliminados", table, deleted);
            }
        }
    }
}
