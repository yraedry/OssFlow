package com.ossflow.coaching.observation.application;

import com.ossflow.coaching.observation.application.port.CoachObservationRepositoryPort;
import com.ossflow.coaching.observation.domain.CoachObservation;
import com.ossflow.coaching.observation.domain.LabelledBy;
import com.ossflow.coaching.observation.domain.RadarRow;
import com.ossflow.coaching.relationship.application.port.CoachAthleteRepositoryPort;
import com.ossflow.shared.exception.ForbiddenException;
import com.ossflow.shared.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CoachObservationService {

    private final CoachObservationRepositoryPort repo;
    private final CoachAthleteRepositoryPort coachAthleteRepo;

    public CoachObservation create(Long coachId, CoachObservation request) {
        if (!coachAthleteRepo.existsByCoachIdAndAthleteId(coachId, request.athleteId())) {
            throw new ForbiddenException("OBSERVATION_NOT_YOUR_ATHLETE", "Not your athlete");
        }
        LabelledBy labelledBy = request.techniqueFamily() != null ? LabelledBy.MANUAL : null;
        Instant now = Instant.now();
        CoachObservation toSave = CoachObservation.builder()
                .coachId(coachId)
                .athleteId(request.athleteId())
                .body(request.body())
                .tone(request.tone())
                .techniqueFamily(request.techniqueFamily())
                .labelledBy(labelledBy)
                .observedAt(now)
                .createdAt(now)
                .build();
        return repo.save(toSave);
    }

    public List<CoachObservation> list(Long coachId, Long athleteId) {
        if (!coachAthleteRepo.existsByCoachIdAndAthleteId(coachId, athleteId)) {
            throw new ForbiddenException("OBSERVATION_NOT_YOUR_ATHLETE", "Not your athlete");
        }
        return repo.findAllByCoachIdAndAthleteIdOrderByObservedAtDesc(coachId, athleteId);
    }

    public void delete(Long coachId, Long id) {
        int deleted = repo.deleteByIdAndCoachId(id, coachId);
        if (deleted == 0) {
            throw new NotFoundException("OBSERVATION_NOT_FOUND", "Observation not found");
        }
    }

    public List<RadarRow> radar(Long coachId, Long athleteId) {
        if (!coachAthleteRepo.existsByCoachIdAndAthleteId(coachId, athleteId)) {
            throw new ForbiddenException("OBSERVATION_NOT_YOUR_ATHLETE", "Not your athlete");
        }
        return repo.aggregateRadar(coachId, athleteId);
    }
}
