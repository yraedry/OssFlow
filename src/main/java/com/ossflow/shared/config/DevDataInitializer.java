package com.ossflow.shared.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("dev")
public class DevDataInitializer implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) {
        // Seeds handled by Flyway V100__seed_federations.sql
    }
}
