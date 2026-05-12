package com.ossflow.planning.weeklytemplate.infrastructure.persistence;

import com.ossflow.planning.weeklytemplate.domain.SessionType;
import jakarta.persistence.*;
import lombok.*;

import java.time.DayOfWeek;
import java.time.Instant;

@Entity
@Table(name = "weekly_template_session")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeeklyTemplateSessionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "template_id", nullable = false)
    private Long templateId;

    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week", nullable = false)
    private DayOfWeek dayOfWeek;

    @Enumerated(EnumType.STRING)
    @Column(name = "session_type", nullable = false)
    private SessionType sessionType;

    @Column(name = "time")
    private String time;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    void prePersist() {
        if (createdAt == null) createdAt = Instant.now();
    }
}
