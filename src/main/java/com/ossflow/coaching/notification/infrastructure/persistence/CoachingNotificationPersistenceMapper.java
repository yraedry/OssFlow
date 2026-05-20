package com.ossflow.coaching.notification.infrastructure.persistence;

import com.ossflow.coaching.notification.domain.CoachingNotification;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CoachingNotificationPersistenceMapper {
    CoachingNotification toDomain(CoachingNotificationEntity entity);
    CoachingNotificationEntity toEntity(CoachingNotification domain);
}
