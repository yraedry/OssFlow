package com.ossflow.coaching.recommendation.infrastructure.persistence;

import com.ossflow.coaching.recommendation.application.port.RecommendationRepositoryPort;
import com.ossflow.coaching.recommendation.domain.RecommendationStatus;
import com.ossflow.coaching.recommendation.domain.TechniqueRecommendation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
@RequiredArgsConstructor
public class RecommendationPersistenceAdapter implements RecommendationRepositoryPort {

    private final RecommendationJpaRepository jpa;
    private final RecommendationMapper mapper;

    @Override
    public TechniqueRecommendation save(TechniqueRecommendation rec) {
        return mapper.toDomain(jpa.save(mapper.toEntity(rec)));
    }

    @Override
    public List<TechniqueRecommendation> findByCoachIdAndAthleteId(Long coachId, Long athleteId) {
        return jpa.findByCoachIdAndAthleteIdOrderByRecommendedAtDesc(coachId, athleteId)
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<TechniqueRecommendation> findReceivedByAthleteId(Long athleteId) {
        return jpa.findReceivedByAthleteId(athleteId)
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public int updateStatusAsCoach(Long id, Long coachId,
                                   RecommendationStatus fromStatus,
                                   RecommendationStatus toStatus,
                                   Instant resolvedAt) {
        return jpa.updateStatusByCoach(id, coachId, fromStatus, toStatus, resolvedAt);
    }

    @Override
    public int updateStatusAsAthlete(Long id, Long athleteId,
                                     RecommendationStatus fromStatus,
                                     RecommendationStatus toStatus,
                                     Instant resolvedAt) {
        return jpa.updateStatusByAthlete(id, athleteId, fromStatus, toStatus, resolvedAt);
    }
}
