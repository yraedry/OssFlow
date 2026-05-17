package com.ossflow.coaching.gym.infrastructure.web.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
public record UpdateGymRequest(@NotBlank @Size(max = 200) String name, String address) {}
