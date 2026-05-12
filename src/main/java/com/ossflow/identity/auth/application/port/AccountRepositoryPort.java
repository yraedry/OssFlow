package com.ossflow.identity.auth.application.port;

import com.ossflow.identity.auth.domain.Account;
import com.ossflow.identity.auth.domain.AccountProvider;

import java.util.Optional;

public interface AccountRepositoryPort {
    Optional<Account> findByEmail(String email);
    Optional<Account> findById(Long id);
    Optional<Account> findByProviderAndProviderId(AccountProvider provider, String providerId);
    Account save(Account account);
    void deleteById(Long id);
}
