package com.ossflow.shared.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "ossflow.flyway")
public record FlywayMigrationProperties(
        @DefaultValue("true") boolean enabled,
        @DefaultValue("false") boolean baselineOnMigrate,
        @DefaultValue("1") String baselineVersion,
        @DefaultValue("false") boolean outOfOrder,
        @DefaultValue("true") boolean validateOnMigrate
) {}
