package com.ossflow.coaching.relationship.infrastructure.web.dto;

import com.ossflow.coaching.relationship.domain.CoachAthleteRelationship;
import com.ossflow.identity.profile.domain.UserProfile;

public record AthleteListItemResponse(Long athleteId, String displayName, String currentBelt, String linkedAt) {
    public static AthleteListItemResponse from(CoachAthleteRelationship r, UserProfile profile) {
        return new AthleteListItemResponse(
            r.athleteId(),
            profile != null ? profile.displayName() : "—",
            profile != null ? profile.currentBelt() : "—",
            r.linkedAt().toString()
        );
    }
}
