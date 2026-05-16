package com.ossflow.coaching.note.application;

import com.ossflow.coaching.note.application.port.AthleteNoteRepositoryPort;
import com.ossflow.coaching.note.domain.AthleteNote;
import com.ossflow.coaching.note.infrastructure.web.dto.CreateNoteRequest;
import com.ossflow.coaching.notification.application.CoachingNotificationService;
import com.ossflow.coaching.relationship.application.port.CoachAthleteRepositoryPort;
import com.ossflow.shared.exception.ForbiddenException;
import com.ossflow.shared.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AthleteNoteService {

    private final AthleteNoteRepositoryPort repo;
    private final CoachAthleteRepositoryPort coachAthleteRepo;
    private final CoachingNotificationService notificationService;

    @Transactional
    public AthleteNote create(Long coachId, CreateNoteRequest request) {
        if (!coachAthleteRepo.existsByCoachIdAndAthleteId(coachId, request.athleteId())) {
            throw new ForbiddenException("NOTE_NOT_YOUR_ATHLETE", "Not your athlete");
        }
        AthleteNote saved = repo.save(AthleteNote.builder()
                .coachId(coachId)
                .athleteId(request.athleteId())
                .body(request.body())
                .techniqueFamily(request.techniqueFamily())
                .createdAt(Instant.now())
                .build());
        notificationService.notifyNoteSent(request.athleteId(), coachId);
        return saved;
    }

    public List<AthleteNote> listForCoach(Long coachId, Long athleteId) {
        if (!coachAthleteRepo.existsByCoachIdAndAthleteId(coachId, athleteId)) {
            throw new ForbiddenException("NOTE_NOT_YOUR_ATHLETE", "Not your athlete");
        }
        return repo.findByCoachIdAndAthleteIdAndDeletedAtIsNullOrderByCreatedAtDesc(coachId, athleteId);
    }

    public void softDelete(Long coachId, Long id) {
        int rows = repo.softDeleteByIdAndCoachId(id, coachId);
        if (rows == 0) {
            throw new NotFoundException("NOTE_NOT_FOUND", "Note not found");
        }
    }

    public List<AthleteNote> listReceived(Long athleteId) {
        return repo.findReceivedByAthleteId(athleteId);
    }

    public AthleteNote getReceivedDetail(Long athleteId, Long id) {
        return repo.findByIdAndAthleteIdAndDeletedAtIsNull(id, athleteId)
                .orElseThrow(() -> new NotFoundException("NOTE_NOT_FOUND", "Note not found"));
    }

    public void markRead(Long athleteId, Long id) {
        int rows = repo.markRead(id, athleteId);
        if (rows == 0) {
            throw new NotFoundException("NOTE_NOT_FOUND", "Note not found");
        }
    }

    public long countUnread(Long athleteId) {
        return repo.countUnreadByAthleteId(athleteId);
    }
}
