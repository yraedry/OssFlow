package com.ossflow.coaching.gym.domain;
import java.time.Instant;
public record GymLocation(Long id, Long coachId, String name, String address, Instant createdAt) {}
