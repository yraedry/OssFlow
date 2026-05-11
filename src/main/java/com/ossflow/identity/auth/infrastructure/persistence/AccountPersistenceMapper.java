package com.ossflow.identity.auth.infrastructure.persistence;

import com.ossflow.identity.auth.domain.Account;
import com.ossflow.identity.auth.domain.AccountProvider;
import org.springframework.stereotype.Component;

@Component
public class AccountPersistenceMapper {

    public Account toDomain(AccountEntity entity) {
        return new Account(
                entity.getId(),
                entity.getEmail(),
                entity.getPasswordHash(),
                entity.getProvider(),
                entity.getProviderId(),
                entity.isEmailVerified(),
                entity.getTokenVersion(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
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
                .createdAt(domain.createdAt())
                .updatedAt(domain.updatedAt())
                .build();
    }
}
