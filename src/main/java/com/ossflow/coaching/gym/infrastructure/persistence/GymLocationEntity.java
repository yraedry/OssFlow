package com.ossflow.coaching.gym.infrastructure.persistence;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.Instant;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Entity @Table(name = "gym_location")
public class GymLocationEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "coach_id", nullable = false)
    private Long coachId;
    @Column(name = "name", nullable = false, length = 200)
    private String name;
    @Column(name = "address", columnDefinition = "TEXT")
    private String address;
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
