package com.ossflow.shared.config;

import com.ossflow.catalog.federation.infrastructure.persistence.FederationEntity;
import com.ossflow.catalog.federation.infrastructure.persistence.FederationJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Profile("dev")
@RequiredArgsConstructor
public class DevDataInitializer implements ApplicationRunner {

    private final FederationJpaRepository federationRepository;

    @Override
    public void run(ApplicationArguments args) {
        if (federationRepository.count() > 0) return;

        List<FederationEntity> federations = List.of(
                federation("IBJJF", "International Brazilian Jiu-Jitsu Federation", "https://ibjjf.com"),
                federation("ADCC", "Abu Dhabi Combat Club", "https://adcombat.com"),
                federation("AJP", "Abu Dhabi Jiu-Jitsu Pro", "https://ajptour.com"),
                federation("NAGA", "North American Grappling Association", "https://nagafighter.com"),
                federation("UAEJJF", "UAE Jiu-Jitsu Federation", "https://uaejjf.org"),
                federation("FEJJB", "Federación Española de Jiu-Jitsu Brasileño", null),
                federation("AEJJ", "Asociación Española de Jiu-Jitsu", null),
                federation("SBJJ", "Spanish Brazilian Jiu-Jitsu", null),
                federation("CBJJE", "Confederación Brasileña de Jiu-Jitsu Esportivo", null),
                federation("GI", "Grappling Industries", "https://grapplingindustries.com")
        );
        federationRepository.saveAll(federations);
    }

    private FederationEntity federation(String code, String name, String url) {
        FederationEntity f = new FederationEntity();
        f.setCode(code);
        f.setName(name);
        f.setOfficialUrl(url);
        return f;
    }
}
