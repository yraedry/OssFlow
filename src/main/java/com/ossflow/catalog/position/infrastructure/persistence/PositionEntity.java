package com.ossflow.catalog.position.infrastructure.persistence;

import com.ossflow.catalog.position.domain.PositionType;
import com.ossflow.catalog.position.domain.Visibility;
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
@Table(name = "position")
@SQLRestriction("deleted_at IS NULL")
public class PositionEntity extends BaseEntity {

    @Column(name = "name", nullable = false, length = 120)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 30)
    private PositionType type;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "youtube_url", length = 500)
    private String youtubeUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "visibility", nullable = false, length = 10)
    private Visibility visibility;
}
