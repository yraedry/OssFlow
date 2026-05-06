package com.ossflow.catalog.system.infrastructure.persistence;

import com.ossflow.catalog.position.domain.Visibility;
import com.ossflow.shared.persistence.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "system")
@SQLRestriction("deleted_at IS NULL")
@Getter
@Setter
public class SystemEntity extends BaseEntity {

    @Column(nullable = false, length = 120)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "anchor_position_id")
    private Long anchorPositionId;

    @Column(name = "flow_definition", nullable = false, columnDefinition = "TEXT")
    private String flowDefinition;

    @Column(name = "flow_schema_version", nullable = false, length = 10)
    private String flowSchemaVersion = "v1";

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Visibility visibility;
}
