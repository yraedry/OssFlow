package com.ossflow.coaching.studyplan.infrastructure.persistence;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Entity @Table(name = "coach_study_block")
public class CoachStudyBlockEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = true)
    private CoachStudyPlanEntity plan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_plan_id")
    private com.ossflow.coaching.classplan.infrastructure.persistence.ClassPlanEntity classPlan;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "block_order", nullable = false)
    private int blockOrder;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @OneToMany(mappedBy = "block", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("itemOrder ASC")
    @Builder.Default
    private List<CoachStudyItemEntity> items = new ArrayList<>();
}
