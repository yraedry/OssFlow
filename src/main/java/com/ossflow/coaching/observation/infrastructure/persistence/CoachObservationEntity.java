package com.ossflow.coaching.observation.infrastructure.persistence;

import com.ossflow.catalog.technique.domain.TechniqueFamily;
import com.ossflow.coaching.observation.domain.LabelledBy;
import com.ossflow.coaching.observation.domain.Tone;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "coach_observation")
@Getter
@Setter
@NoArgsConstructor
public class CoachObservationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "coach_id", nullable = false)
    private Long coachId;

    @Column(name = "athlete_id", nullable = false)
    private Long athleteId;

    @Column(name = "body", nullable = false)
    private String body;

    @Enumerated(EnumType.STRING)
    @Column(name = "tone", nullable = false, length = 10)
    private Tone tone;

    @Enumerated(EnumType.STRING)
    @Column(name = "technique_family", length = 30)
    private TechniqueFamily techniqueFamily;

    @Enumerated(EnumType.STRING)
    @Column(name = "labelled_by", length = 20)
    private LabelledBy labelledBy;

    @Column(name = "observed_at", nullable = false)
    private Instant observedAt;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
}
