package com.ossflow.coaching.gym.infrastructure.web.dto;
import com.ossflow.coaching.gym.domain.GymLocation;
import java.time.Instant;
public record GymResponse(Long id, String name, String address, Instant createdAt) {
    public static GymResponse from(GymLocation g) {
        return new GymResponse(g.id(), g.name(), g.address(), g.createdAt());
    }
}
