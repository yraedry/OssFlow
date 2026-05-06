package com.ossflow.journal.note.infrastructure.persistence;

import com.ossflow.journal.tag.infrastructure.persistence.TagEntity;
import com.ossflow.shared.persistence.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "note")
@SQLRestriction("deleted_at IS NULL")
public class NoteEntity extends BaseEntity {

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "body_markdown", columnDefinition = "TEXT")
    private String bodyMarkdown;

    @Column(name = "target_type", length = 50)
    private String targetType;

    @Column(name = "target_id")
    private Long targetId;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "note_tag",
            joinColumns = @JoinColumn(name = "note_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    @Builder.Default
    private List<TagEntity> tags = new ArrayList<>();
}
