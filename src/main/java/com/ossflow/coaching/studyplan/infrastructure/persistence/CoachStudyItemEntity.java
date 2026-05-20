package com.ossflow.coaching.studyplan.infrastructure.persistence;

import com.ossflow.coaching.studyplan.domain.StudyItemType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Entity @Table(name = "coach_study_item")
public class CoachStudyItemEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "block_id", nullable = false)
    private CoachStudyBlockEntity block;

    @Column(name = "item_order", nullable = false)
    private int itemOrder;

    @Enumerated(EnumType.STRING)
    @Column(name = "item_type", nullable = false, length = 20)
    private StudyItemType itemType;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "technique_id")
    private Long techniqueId;

    @Column(name = "technique_name", length = 255)
    private String techniqueName;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
