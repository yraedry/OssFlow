package com.ossflow.planning.weeklytemplate.infrastructure.persistence;

import com.ossflow.shared.persistence.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "weekly_template",
       uniqueConstraints = @UniqueConstraint(columnNames = "owner_id"))
@Getter
@Setter
@Builder
public class WeeklyTemplateEntity extends BaseEntity {
    // sessions stored in weekly_template_session
}
