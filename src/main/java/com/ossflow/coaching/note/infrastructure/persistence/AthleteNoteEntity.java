package com.ossflow.coaching.note.infrastructure.persistence;

import com.ossflow.catalog.technique.domain.TechniqueFamily;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "athlete_note")
@Getter
@Setter
@NoArgsConstructor
public class AthleteNoteEntity {

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
    @Column(name = "technique_family", length = 30)
    private TechniqueFamily techniqueFamily;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    @Column(name = "read_at")
    private Instant readAt;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
}
