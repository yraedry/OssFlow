package com.ossflow.planning.studyblock.infrastructure.persistence;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "study_block")
@EntityListeners(AuditingEntityListener.class)
public class StudyBlockEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "study_plan_id", nullable = false)
    private Long studyPlanId;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "block_order", nullable = false)
    private int blockOrder;

    @Column(name = "notes_markdown", columnDefinition = "TEXT")
    private String notesMarkdown;

    @Column(name = "focus_entities", columnDefinition = "TEXT")
    private String focusEntities;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
