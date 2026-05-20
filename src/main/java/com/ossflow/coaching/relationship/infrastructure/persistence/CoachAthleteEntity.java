package com.ossflow.coaching.relationship.infrastructure.persistence;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.Instant;

@Entity
@Table(name = "coach_athlete")
@Getter @Setter @NoArgsConstructor
public class CoachAthleteEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "coach_id", nullable = false)
    private Long coachId;
    @Column(name = "athlete_id", nullable = false)
    private Long athleteId;
    @Column(name = "invitation_id")
    private Long invitationId;
    @Column(name = "linked_at", nullable = false)
    private Instant linkedAt;
}
