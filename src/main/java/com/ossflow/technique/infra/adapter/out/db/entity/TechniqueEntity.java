package com.ossflow.technique.infra.adapter.out.db.entity;

import com.ossflow.technique.domain.model.Belt;
import com.ossflow.technique.domain.model.Modality;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "techniques")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TechniqueEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String category;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "youtube_url")
    private String youtubeUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "minimum_belt")
    private Belt minimumBelt;

    @Enumerated(EnumType.STRING)
    @Column(name = "modality", nullable = false)
    private Modality modality;

    @ManyToOne
    @JoinColumn(name = "start_position_id", nullable = false)
    private PositionEntity startPosition;

    @ManyToOne
    @JoinColumn(name = "end_position_id")
    private PositionEntity endPosition;
}