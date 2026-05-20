package com.ossflow.identity.profile.infrastructure.web;

import com.ossflow.identity.profile.domain.UserProfile;
import com.ossflow.identity.profile.domain.UserProfileFederation;
import com.ossflow.identity.profile.infrastructure.web.dto.CreateUserProfileRequest;
import com.ossflow.identity.profile.infrastructure.web.dto.UpdateUserProfileRequest;
import com.ossflow.identity.profile.infrastructure.web.dto.UserProfileFederationRequest;
import com.ossflow.identity.profile.infrastructure.web.dto.UserProfileFederationResponse;
import com.ossflow.identity.profile.infrastructure.web.dto.UserProfileResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserProfileWebMapper {

    @Mapping(target = "role", ignore = true)
    UserProfileResponse toResponse(UserProfile profile);

    UserProfileFederationResponse toFederationResponse(UserProfileFederation federation);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ownerId", ignore = true)
    @Mapping(target = "federations", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "onboardingCompleted", constant = "false")
    UserProfile fromCreate(CreateUserProfileRequest req);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ownerId", ignore = true)
    @Mapping(target = "federations", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "onboardingCompleted", ignore = true)
    UserProfile fromUpdate(UpdateUserProfileRequest req);

    UserProfileFederation fromFederationRequest(UserProfileFederationRequest req);
}
