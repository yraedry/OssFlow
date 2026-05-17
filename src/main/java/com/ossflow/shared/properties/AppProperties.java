package com.ossflow.shared.properties;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app")
public record AppProperties(
        @NotBlank String frontendUrl,
        @Valid CookieProperties cookie,
        @Valid RefreshTokenProperties refreshToken
) {
    public record CookieProperties(
            boolean secure,
            @NotBlank String sameSite,
            @NotBlank String path
    ) {}

    public record RefreshTokenProperties(
            @Positive long expiry
    ) {}
}
