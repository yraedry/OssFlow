package com.ossflow.identity.auth.application;

import com.ossflow.identity.auth.domain.AccountEventType;
import com.ossflow.identity.auth.infrastructure.persistence.AccountEventEntity;
import com.ossflow.identity.auth.infrastructure.persistence.AccountEventJpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Instant;

/**
 * S1.5: Audit log de eventos de cuenta.
 * Los eventos se persisten de forma asíncrona para no bloquear el flujo de autenticación.
 */
@Service
public class AccountEventService {

    private static final Logger log = LoggerFactory.getLogger(AccountEventService.class);

    private final AccountEventJpaRepository repository;

    public AccountEventService(AccountEventJpaRepository repository) {
        this.repository = repository;
    }

    @Async
    public void record(Long accountId, AccountEventType eventType,
                       String ipAddress, String userAgent) {
        try {
            repository.save(new AccountEventEntity(
                    accountId,
                    eventType.name(),
                    truncate(ipAddress, 45),
                    truncate(userAgent, 255),
                    Instant.now()
            ));
        } catch (Exception e) {
            // El audit log no debe interrumpir el flujo de negocio.
            log.error("Failed to record account event {} for account {}: {}",
                    eventType, accountId, e.getMessage());
        }
    }

    private String truncate(String value, int maxLen) {
        if (value == null) return null;
        return value.length() <= maxLen ? value : value.substring(0, maxLen);
    }
}
