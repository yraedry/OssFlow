package com.ossflow.shared.config;

import com.ossflow.shared.properties.FlywayMigrationProperties;
import org.flywaydb.core.Flyway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class FlywayConfig {

    @Bean
    public Flyway flyway(DataSource dataSource, FlywayMigrationProperties props) {
        Flyway flyway = Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:db/migration")
                .baselineOnMigrate(props.baselineOnMigrate())
                .baselineVersion(props.baselineVersion())
                .outOfOrder(props.outOfOrder())
                .validateOnMigrate(props.validateOnMigrate())
                .load();
        if (props.enabled()) {
            flyway.migrate();
        }
        return flyway;
    }
}
