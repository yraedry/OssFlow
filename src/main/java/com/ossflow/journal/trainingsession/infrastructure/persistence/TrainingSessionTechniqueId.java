package com.ossflow.journal.trainingsession.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class TrainingSessionTechniqueId implements Serializable {

    @Column(name = "training_session_id")
    private Long trainingSessionId;

    @Column(name = "technique_id")
    private Long techniqueId;
}
