package com.ossflow.identity.auth.infrastructure.web.dto;

public record AuthResponse(
        String accessToken,
        UserDto user
) {
    public record UserDto(Long id, String email) {}
}
