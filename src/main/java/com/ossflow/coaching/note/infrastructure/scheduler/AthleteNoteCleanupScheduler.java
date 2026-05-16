package com.ossflow.coaching.note.infrastructure.scheduler;

import com.ossflow.coaching.note.application.port.AthleteNoteRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Component
@RequiredArgsConstructor
public class AthleteNoteCleanupScheduler {

    private final AthleteNoteRepositoryPort repo;

    @Scheduled(cron = "0 0 3 * * *")
    public void purgeOldDeletedNotes() {
        repo.purgeOlderThan(Instant.now().minus(30, ChronoUnit.DAYS));
    }
}
