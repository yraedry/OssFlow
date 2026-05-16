package com.ossflow.coaching.note.infrastructure.scheduler;

import com.ossflow.coaching.note.application.port.AthleteNoteRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class AthleteNoteCleanupScheduler {

    private final AthleteNoteRepositoryPort repo;

    @Scheduled(cron = "0 0 3 * * *")
    public void purgeOldDeletedNotes() {
        try {
            int purged = repo.purgeOlderThan(Instant.now().minus(30, ChronoUnit.DAYS));
            log.info("Purged {} soft-deleted notes", purged);
        } catch (Exception e) {
            log.error("Failed to purge old notes", e);
        }
    }
}
