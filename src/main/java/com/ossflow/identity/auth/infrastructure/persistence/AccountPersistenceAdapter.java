package com.ossflow.identity.auth.infrastructure.persistence;

import com.ossflow.identity.auth.application.port.AccountRepositoryPort;
import com.ossflow.identity.auth.domain.Account;
import com.ossflow.identity.auth.domain.AccountProvider;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AccountPersistenceAdapter implements AccountRepositoryPort {

    private final AccountJpaRepository jpaRepository;
    private final AccountPersistenceMapper mapper;

    public AccountPersistenceAdapter(AccountJpaRepository jpaRepository, AccountPersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<Account> findByEmail(String email) {
        return jpaRepository.findByEmail(email).map(mapper::toDomain);
    }

    @Override
    public Optional<Account> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Account> findByProviderAndProviderId(AccountProvider provider, String providerId) {
        return jpaRepository.findByProviderAndProviderId(provider, providerId).map(mapper::toDomain);
    }

    @Override
    public Account save(Account account) {
        AccountEntity entity = mapper.toEntity(account);
        return mapper.toDomain(jpaRepository.save(entity));
    }
}
