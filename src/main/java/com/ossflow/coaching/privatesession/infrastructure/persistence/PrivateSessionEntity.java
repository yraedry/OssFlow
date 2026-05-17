package com.ossflow.coaching.privatesession.infrastructure.persistence;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "private_session")
@Getter
@Setter
@NoArgsConstructor
public class PrivateSessionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "coach_id", nullable = false)
    private Long coachId;

    @Column(name = "athlete_id", nullable = false)
    private Long athleteId;

    @Column(name = "gym_id")
    private Long gymId;

    @Column(name = "session_date", nullable = false)
    private LocalDate sessionDate;

    @Column(name = "start_time")
    private LocalTime startTime;

    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    @Column(name = "title", length = 200)
    private String title;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "techniques_worked", columnDefinition = "text[]")
    private List<String> techniquesWorked = new ArrayList<>();

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
}
