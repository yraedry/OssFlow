package com.ossflow.catalog.technique.infrastructure.persistence;

import com.ossflow.catalog.position.domain.Visibility;
import com.ossflow.catalog.technique.domain.Belt;
import com.ossflow.catalog.technique.domain.Modality;
import com.ossflow.catalog.technique.domain.TechniqueCategory;
import com.ossflow.catalog.technique.domain.TechniqueFamily;
import com.ossflow.shared.persistence.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "technique")
@SQLRestriction("deleted_at IS NULL")
public class TechniqueEntity extends BaseEntity {

    @Column(name = "name", nullable = false, length = 120)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 30)
    private TechniqueCategory category;

    @Enumerated(EnumType.STRING)
    @Column(name = "family", length = 30)
    private TechniqueFamily family;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "youtube_url", length = 500)
    private String youtubeUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "minimum_belt", nullable = false, length = 15)
    private Belt minimumBelt;

    @Enumerated(EnumType.STRING)
    @Column(name = "modality", nullable = false, length = 10)
    private Modality modality;

    @Column(name = "start_position_id", nullable = false)
    private Long startPositionId;

    @Column(name = "end_position_id")
    private Long endPositionId;

    @Enumerated(EnumType.STRING)
    @Column(name = "visibility", nullable = false, length = 10)
    private Visibility visibility;
}
