package com.ossflow.coaching.recommendation.application.port;

import com.ossflow.coaching.recommendation.domain.RecommendationStatus;
import com.ossflow.coaching.recommendation.domain.TechniqueRecommendation;

import java.time.Instant;
import java.util.List;

public interface RecommendationRepositoryPort {

    TechniqueRecommendation save(TechniqueRecommendation rec);

    List<TechniqueRecommendation> findByCoachIdAndAthleteId(Long coachId, Long athleteId);

    List<TechniqueRecommendation> findReceivedByAthleteId(Long athleteId);

    int updateStatusAsCoach(Long id, Long coachId,
                            RecommendationStatus fromStatus,
                            RecommendationStatus toStatus,
                            Instant resolvedAt);

    int updateStatusAsAthlete(Long id, Long athleteId,
                              RecommendationStatus fromStatus,
                              RecommendationStatus toStatus,
                              Instant resolvedAt);
}
