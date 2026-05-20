package com.ossflow.coaching.relationship.infrastructure.web.dto;

import com.ossflow.coaching.relationship.domain.CoachAthleteRelationship;
import com.ossflow.identity.profile.domain.UserProfile;

public record CoachListItemResponse(Long coachId, String displayName, String academy) {
    public static CoachListItemResponse from(CoachAthleteRelationship r, UserProfile profile) {
        return new CoachListItemResponse(
            r.coachId(),
            profile != null ? profile.displayName() : "—",
            profile != null ? profile.academy() : null
        );
    }
}
