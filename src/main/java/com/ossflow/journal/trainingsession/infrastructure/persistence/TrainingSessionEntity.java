package com.ossflow.journal.trainingsession.infrastructure.persistence;

import com.ossflow.journal.trainingsession.domain.Intensity;
import com.ossflow.shared.persistence.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "training_session")
@SQLRestriction("deleted_at IS NULL")
public class TrainingSessionEntity extends BaseEntity {

    @Column(name = "session_date", nullable = false)
    private LocalDate sessionDate;

    @Column(name = "duration_minutes", nullable = false)
    private int durationMinutes;

    @Column(name = "location", length = 200)
    private String location;

    @Enumerated(EnumType.STRING)
    @Column(name = "intensity", nullable = false, length = 15)
    private Intensity intensity;

    @Column(name = "notes_markdown", columnDefinition = "TEXT")
    private String notesMarkdown;

    @OneToMany(mappedBy = "trainingSession", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<TrainingSessionTechniqueEntity> workedTechniques = new ArrayList<>();
}
