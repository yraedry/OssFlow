package com.ossflow.coaching.invitation.infrastructure.persistence;

import com.ossflow.coaching.invitation.domain.CoachInvitation;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CoachInvitationPersistenceMapper {
    CoachInvitation toDomain(CoachInvitationEntity entity);
    CoachInvitationEntity toEntity(CoachInvitation domain);
}
