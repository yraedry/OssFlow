package com.ossflow.coaching.privatesession.application;

import com.ossflow.coaching.privatesession.application.port.PrivateSessionRepositoryPort;
import com.ossflow.coaching.privatesession.domain.PrivateSession;
import com.ossflow.coaching.privatesession.infrastructure.web.dto.CreatePrivateSessionRequest;
import com.ossflow.coaching.privatesession.infrastructure.web.dto.UpdatePrivateSessionRequest;
import com.ossflow.coaching.relationship.application.port.CoachAthleteRepositoryPort;
import com.ossflow.shared.exception.ForbiddenException;
import com.ossflow.shared.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PrivateSessionService {

    private final PrivateSessionRepositoryPort repo;
    private final CoachAthleteRepositoryPort coachAthleteRepo;

    @Transactional
    public PrivateSession create(Long coachId, CreatePrivateSessionRequest request) {
        requireLinked(coachId, request.athleteId());
        return repo.save(PrivateSession.builder()
                .coachId(coachId)
                .athleteId(request.athleteId())
                .gymId(request.gymId())
                .sessionDate(request.sessionDate())
                .startTime(request.startTime())
                .durationMinutes(request.durationMinutes())
                .title(request.title())
                .notes(request.notes())
                .techniquesWorked(Objects.requireNonNullElse(request.techniquesWorked(), List.of()))
                .createdAt(Instant.now())
                .build());
    }

    public List<PrivateSession> listByAthlete(Long coachId, Long athleteId) {
        requireLinked(coachId, athleteId);
        return repo.findByCoachIdAndAthleteIdOrderBySessionDateDesc(coachId, athleteId);
    }

    public List<PrivateSession> listAll(Long coachId) {
        return repo.findByCoachIdOrderBySessionDateDesc(coachId);
    }

    @Transactional
    public PrivateSession update(Long coachId, Long sessionId, UpdatePrivateSessionRequest request) {
        PrivateSession existing = repo.findByIdAndCoachId(sessionId, coachId)
                .orElseThrow(() -> new NotFoundException("SESSION_NOT_FOUND", "Session not found"));

        PrivateSession updated = existing.toBuilder()
                .gymId(request.gymId() != null ? request.gymId() : existing.gymId())
                .sessionDate(request.sessionDate() != null ? request.sessionDate() : existing.sessionDate())
                .startTime(request.startTime() != null ? request.startTime() : existing.startTime())
                .durationMinutes(request.durationMinutes() != null ? request.durationMinutes() : existing.durationMinutes())
                .title(request.title() != null ? request.title() : existing.title())
                .notes(request.notes() != null ? request.notes() : existing.notes())
                .techniquesWorked(request.techniquesWorked() != null ? request.techniquesWorked() : existing.techniquesWorked())
                .build();

        return repo.save(updated);
    }

    @Transactional
    public void delete(Long coachId, Long sessionId) {
        int rows = repo.deleteByIdAndCoachId(sessionId, coachId);
        if (rows == 0) {
            throw new NotFoundException("SESSION_NOT_FOUND", "Session not found");
        }
    }

    public List<PrivateSession> listMine(Long athleteId) {
        return repo.findByAthleteIdOrderBySessionDateDesc(athleteId);
    }

    private void requireLinked(Long coachId, Long athleteId) {
        if (!coachAthleteRepo.existsByCoachIdAndAthleteId(coachId, athleteId)) {
            throw new ForbiddenException("SESSION_NOT_YOUR_ATHLETE", "Not your athlete");
        }
    }
}
