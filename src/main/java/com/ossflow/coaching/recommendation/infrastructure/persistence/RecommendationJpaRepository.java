package com.ossflow.coaching.recommendation.infrastructure.persistence;

import com.ossflow.coaching.recommendation.domain.RecommendationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

interface RecommendationJpaRepository extends JpaRepository<RecommendationEntity, Long> {

    List<RecommendationEntity> findByCoachIdAndAthleteIdOrderByRecommendedAtDesc(Long coachId, Long athleteId);

    @Query("SELECT r FROM RecommendationEntity r WHERE r.athleteId = :athleteId AND r.status <> 'CANCELLED' ORDER BY r.recommendedAt DESC")
    List<RecommendationEntity> findReceivedByAthleteId(@Param("athleteId") Long athleteId);

    @Modifying
    @Transactional
    @Query("UPDATE RecommendationEntity r SET r.status = :toStatus, r.resolvedAt = :resolvedAt WHERE r.id = :id AND r.coachId = :ownerId AND r.status = :fromStatus")
    int updateStatusByCoach(@Param("id") Long id,
                            @Param("ownerId") Long ownerId,
                            @Param("fromStatus") RecommendationStatus fromStatus,
                            @Param("toStatus") RecommendationStatus toStatus,
                            @Param("resolvedAt") Instant resolvedAt);

    @Modifying
    @Transactional
    @Query("UPDATE RecommendationEntity r SET r.status = :toStatus, r.resolvedAt = :resolvedAt WHERE r.id = :id AND r.athleteId = :ownerId AND r.status = :fromStatus")
    int updateStatusByAthlete(@Param("id") Long id,
                              @Param("ownerId") Long ownerId,
                              @Param("fromStatus") RecommendationStatus fromStatus,
                              @Param("toStatus") RecommendationStatus toStatus,
                              @Param("resolvedAt") Instant resolvedAt);
}
