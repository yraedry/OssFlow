package com.ossflow.coaching.recommendation.infrastructure.persistence;

import com.ossflow.coaching.recommendation.domain.RecommendationStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "technique_recommendation")
@Getter
@Setter
@NoArgsConstructor
public class RecommendationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "coach_id", nullable = false)
    private Long coachId;

    @Column(name = "athlete_id", nullable = false)
    private Long athleteId;

    @Column(name = "technique_id", nullable = false)
    private Long techniqueId;

    @Column(name = "note", length = 1000)
    private String note;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private RecommendationStatus status;

    @Column(name = "recommended_at", nullable = false)
    private Instant recommendedAt;

    @Column(name = "resolved_at")
    private Instant resolvedAt;
}
