package com.ossflow.journal.trainingsession.infrastructure.persistence;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "training_session_technique")
@Getter
@Setter
public class TrainingSessionTechniqueEntity {

    @EmbeddedId
    private TrainingSessionTechniqueId id = new TrainingSessionTechniqueId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("trainingSessionId")
    @JoinColumn(name = "training_session_id")
    private TrainingSessionEntity trainingSession;

    @Column(name = "rep_count")
    private Integer repCount;

    @Column(name = "notes_markdown", columnDefinition = "TEXT")
    private String notesMarkdown;
}
