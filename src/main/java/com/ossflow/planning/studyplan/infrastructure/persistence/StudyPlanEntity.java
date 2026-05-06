package com.ossflow.planning.studyplan.infrastructure.persistence;

import com.ossflow.planning.studyplan.domain.StudyPlanStatus;
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
@Table(name = "study_plan")
@SQLRestriction("deleted_at IS NULL")
public class StudyPlanEntity extends BaseEntity {

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "goal_markdown", columnDefinition = "TEXT")
    private String goalMarkdown;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private StudyPlanStatus status;
}
