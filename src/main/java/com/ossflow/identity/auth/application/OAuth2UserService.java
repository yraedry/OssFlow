package com.ossflow.identity.auth.application;

import com.ossflow.identity.auth.application.port.AccountRepositoryPort;
import com.ossflow.identity.auth.domain.Account;
import com.ossflow.identity.auth.domain.AccountProvider;
import com.ossflow.identity.auth.infrastructure.security.AccountPrincipal;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
public class OAuth2UserService extends DefaultOAuth2UserService {

    private final AccountRepositoryPort accountRepository;

    public OAuth2UserService(AccountRepositoryPort accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauthUser = super.loadUser(userRequest);
        Map<String, Object> attributes = oauthUser.getAttributes();

        String providerId = (String) attributes.get("sub");
        String email = (String) attributes.get("email");

        Account account = accountRepository.findByProviderAndProviderId(AccountProvider.GOOGLE, providerId)
                .or(() -> accountRepository.findByEmail(email))
                .map(existing -> {
                    // If found by email but different provider, link Google
                    if (existing.provider() == AccountProvider.LOCAL && existing.providerId() == null) {
                        return accountRepository.save(new Account(
                                existing.id(), existing.email(), existing.passwordHash(),
                                AccountProvider.GOOGLE, providerId, true, existing.tokenVersion(),
                                existing.createdAt(), existing.updatedAt()
                        ));
                    }
                    return existing;
                })
                .orElseGet(() -> accountRepository.save(new Account(
                        null, email, null, AccountProvider.GOOGLE, providerId,
                        true, 0, null, null
                )));

        return new DefaultOAuth2User(
                new AccountPrincipal(account.id(), account.email()).getAuthorities(),
                Map.of("sub", providerId, "email", email, "accountId", account.id()),
                "sub"
        );
    }
}
