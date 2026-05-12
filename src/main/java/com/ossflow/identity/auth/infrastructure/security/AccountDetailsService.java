package com.ossflow.identity.auth.infrastructure.security;

import com.ossflow.identity.auth.application.port.AccountRepositoryPort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AccountDetailsService implements UserDetailsService {

    private final AccountRepositoryPort accountRepository;

    public AccountDetailsService(AccountRepositoryPort accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return accountRepository.findByEmail(email)
                .map(account -> new AccountPrincipal(account.id(), account.email()))
                .orElseThrow(() -> new UsernameNotFoundException("Account not found: " + email));
    }
}
