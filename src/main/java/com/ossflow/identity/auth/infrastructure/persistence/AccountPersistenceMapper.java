package com.ossflow.identity.auth.infrastructure.persistence;

import com.ossflow.identity.auth.domain.Account;
import com.ossflow.identity.auth.domain.AccountProvider;
import org.springframework.stereotype.Component;

@Component
public class AccountPersistenceMapper {

    public Account toDomain(AccountEntity entity) {
        return Account.builder()
                .id(entity.getId())
                .email(entity.getEmail())
                .passwordHash(entity.getPasswordHash())
                .provider(entity.getProvider())
                .providerId(entity.getProviderId())
                .emailVerified(entity.isEmailVerified())
                .tokenVersion(entity.getTokenVersion())
                .role(entity.getRole())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public AccountEntity toEntity(Account domain) {
        return AccountEntity.builder()
                .id(domain.id())
                .email(domain.email())
                .passwordHash(domain.passwordHash())
                .provider(domain.provider())
                .providerId(domain.providerId())
                .emailVerified(domain.emailVerified())
                .tokenVersion(domain.tokenVersion())
                .role(domain.role())
                .createdAt(domain.createdAt())
                .updatedAt(domain.updatedAt())
                .build();
    }
}
