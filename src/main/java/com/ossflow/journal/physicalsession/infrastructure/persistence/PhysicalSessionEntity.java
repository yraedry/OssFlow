package com.ossflow.journal.physicalsession.infrastructure.persistence;

import com.ossflow.journal.physicalsession.domain.PhysicalSessionType;
import com.ossflow.shared.persistence.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "physical_session")
@SQLRestriction("deleted_at IS NULL")
public class PhysicalSessionEntity extends BaseEntity {

    @Column(name = "session_date", nullable = false)
    private LocalDate sessionDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "session_type", nullable = false, length = 20)
    private PhysicalSessionType sessionType;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
}
