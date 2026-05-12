package com.ossflow.identity.auth.application;

import com.ossflow.identity.auth.application.port.AccountRepositoryPort;
import com.ossflow.planning.weeklytemplate.application.port.WeeklyTemplateRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountDeletionService {

    private final AccountRepositoryPort accountRepository;
    private final WeeklyTemplateRepositoryPort weeklyTemplateRepository;

    @Transactional
    public void deleteAccount(Long accountId) {
        // Borrar datos de planificación (weekly_template_session tiene CASCADE desde weekly_template)
        weeklyTemplateRepository.deleteByOwnerId(accountId);

        // Borrar la cuenta — user_profile tiene ON DELETE CASCADE → V252
        accountRepository.deleteById(accountId);

        log.info("Account {} deleted with all associated data", accountId);
    }
}
