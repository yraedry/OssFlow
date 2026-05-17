package com.ossflow.shared.config;

import org.flywaydb.core.Flyway;
import org.springframework.boot.autoconfigure.flyway.FlywayProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
@EnableConfigurationProperties(FlywayProperties.class)
public class FlywayConfig {

    @Bean
    public Flyway flyway(DataSource dataSource, FlywayProperties props) {
        Flyway flyway = Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:db/migration")
                .baselineOnMigrate(props.isBaselineOnMigrate())
                .baselineVersion(props.getBaselineVersion())
                .outOfOrder(props.isOutOfOrder())
                .validateOnMigrate(props.isValidateOnMigrate())
                .load();
        if (props.isEnabled()) {
            flyway.migrate();
        }
        return flyway;
    }
}
