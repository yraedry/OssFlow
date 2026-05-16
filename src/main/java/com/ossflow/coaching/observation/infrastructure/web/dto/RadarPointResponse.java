package com.ossflow.coaching.observation.infrastructure.web.dto;

import com.ossflow.coaching.observation.domain.RadarRow;

public record RadarPointResponse(String family, long score) {
    public static RadarPointResponse from(RadarRow row) {
        return new RadarPointResponse(row.family().name(), row.score());
    }
}
