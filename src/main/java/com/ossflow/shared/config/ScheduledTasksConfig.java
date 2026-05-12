package com.ossflow.shared.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.time.Clock;

@Configuration
@EnableScheduling
@EnableAsync
public class ScheduledTasksConfig {

    @Bean
    public Clock systemClock() {
        return Clock.systemUTC();
    }
}
