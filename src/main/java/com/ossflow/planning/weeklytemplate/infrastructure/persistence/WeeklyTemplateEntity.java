package com.ossflow.planning.weeklytemplate.infrastructure.persistence;

import com.ossflow.planning.weeklytemplate.domain.DayEntry;
import com.ossflow.shared.persistence.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "weekly_template",
       uniqueConstraints = @UniqueConstraint(columnNames = "owner_id"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeeklyTemplateEntity extends BaseEntity {

    @Convert(converter = DayEntryListConverter.class)
    @Column(name = "days_json", columnDefinition = "TEXT")
    private List<DayEntry> days;
}
