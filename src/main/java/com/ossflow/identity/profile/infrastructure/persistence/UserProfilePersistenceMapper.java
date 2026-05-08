package com.ossflow.identity.profile.infrastructure.persistence;

import com.ossflow.identity.profile.domain.UserProfile;
import com.ossflow.identity.profile.domain.UserProfileFederation;
import com.ossflow.shared.persistence.InstantOffsetDateTimeMapper;
import org.mapstruct.BeanMapping;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = InstantOffsetDateTimeMapper.class)
public interface UserProfilePersistenceMapper {

    @BeanMapping(builder = @Builder(disableBuilder = true))
    @Mapping(target = "federations", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    UserProfileEntity toEntity(UserProfile domain);

    @Mapping(target = "federations", source = "federations")
    @Mapping(target = "createdAt", expression = "java(entity.getCreatedAtInstant())")
    @Mapping(target = "updatedAt", expression = "java(entity.getUpdatedAtInstant())")
    UserProfile toDomain(UserProfileEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userProfile", ignore = true)
    @Mapping(target = "isPrimary", source = "isPrimary")
    UserProfileFederationEntity toFederationEntity(UserProfileFederation domain);

    @Mapping(target = "federationId", source = "id.federationId")
    @Mapping(target = "isPrimary", source = "primary")
    UserProfileFederation toFederationDomain(UserProfileFederationEntity entity);
}
