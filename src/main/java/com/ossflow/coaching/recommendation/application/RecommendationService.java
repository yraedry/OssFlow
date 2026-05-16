package com.ossflow.coaching.recommendation.application;

import com.ossflow.catalog.technique.application.port.TechniqueRepositoryPort;
import com.ossflow.coaching.notification.application.CoachingNotificationService;
import com.ossflow.coaching.recommendation.application.port.RecommendationRepositoryPort;
import com.ossflow.coaching.recommendation.domain.RecommendationStatus;
import com.ossflow.coaching.recommendation.domain.TechniqueRecommendation;
import com.ossflow.coaching.recommendation.infrastructure.web.dto.CreateRecommendationRequest;
import com.ossflow.coaching.relationship.application.port.CoachAthleteRepositoryPort;
import com.ossflow.identity.profile.application.port.UserProfileRepositoryPort;
import com.ossflow.shared.exception.ForbiddenException;
import com.ossflow.shared.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final RecommendationRepositoryPort repo;
    private final CoachAthleteRepositoryPort coachAthleteRepo;
    private final CoachingNotificationService notificationService;
    private final UserProfileRepositoryPort profileRepo;
    private final TechniqueRepositoryPort techniqueRepo;

    @Transactional
    public TechniqueRecommendation create(Long coachId, CreateRecommendationRequest req) {
        if (!coachAthleteRepo.existsByCoachIdAndAthleteId(coachId, req.athleteId())) {
            throw new ForbiddenException("RECOMMENDATION_NOT_YOUR_ATHLETE", "Not your athlete");
        }

        var technique = techniqueRepo.findById(req.techniqueId(), null)
                .orElseThrow(() -> new NotFoundException("TECHNIQUE_NOT_FOUND", "Technique not found"));

        String coachName = profileRepo.findByOwnerId(coachId)
                .map(p -> p.displayName() != null ? p.displayName() : "Tu maestro")
                .orElse("Tu maestro");

        var saved = repo.save(TechniqueRecommendation.builder()
                .coachId(coachId)
                .athleteId(req.athleteId())
                .techniqueId(req.techniqueId())
                .note(req.note())
                .status(RecommendationStatus.PENDING)
                .recommendedAt(Instant.now())
                .build());

        notificationService.notifyRecommendationSent(req.athleteId(), coachName, technique.name());
        return saved;
    }

    public List<TechniqueRecommendation> listSent(Long coachId, Long athleteId) {
        if (!coachAthleteRepo.existsByCoachIdAndAthleteId(coachId, athleteId)) {
            throw new ForbiddenException("RECOMMENDATION_NOT_YOUR_ATHLETE", "Not your athlete");
        }
        return repo.findByCoachIdAndAthleteId(coachId, athleteId);
    }

    @Transactional
    public void cancel(Long coachId, Long id) {
        int rows = repo.updateStatusAsCoach(id, coachId,
                RecommendationStatus.PENDING, RecommendationStatus.CANCELLED, Instant.now());
        if (rows == 0) {
            throw new NotFoundException("RECOMMENDATION_NOT_FOUND", "Recommendation not found");
        }
    }

    public List<TechniqueRecommendation> listReceived(Long athleteId) {
        return repo.findReceivedByAthleteId(athleteId);
    }

    @Transactional
    public void accept(Long athleteId, Long id) {
        int rows = repo.updateStatusAsAthlete(id, athleteId,
                RecommendationStatus.PENDING, RecommendationStatus.ACCEPTED, Instant.now());
        if (rows == 0) {
            throw new NotFoundException("RECOMMENDATION_NOT_FOUND", "Recommendation not found");
        }
    }

    @Transactional
    public void dismiss(Long athleteId, Long id) {
        int rows = repo.updateStatusAsAthlete(id, athleteId,
                RecommendationStatus.PENDING, RecommendationStatus.DISMISSED, Instant.now());
        if (rows == 0) {
            throw new NotFoundException("RECOMMENDATION_NOT_FOUND", "Recommendation not found");
        }
    }
}
