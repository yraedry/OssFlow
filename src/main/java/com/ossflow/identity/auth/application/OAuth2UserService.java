package com.ossflow.identity.auth.application;

import com.ossflow.identity.auth.application.port.AccountRepositoryPort;
import com.ossflow.identity.auth.domain.Account;
import com.ossflow.identity.auth.domain.AccountProvider;
import com.ossflow.identity.auth.infrastructure.security.AccountPrincipal;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
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
        Object emailVerifiedRaw = attributes.get("email_verified");

        if (!Boolean.TRUE.equals(emailVerifiedRaw)) {
            throw new OAuth2AuthenticationException(
                    new OAuth2Error("EMAIL_NOT_VERIFIED", "Email no verificado por el proveedor", null));
        }

        // Linking silencioso prohibido: si hay cuenta local con ese email, exigir login local primero.
        // Linking explícito con confirmación es feature futura.
        var byProvider = accountRepository.findByProviderAndProviderId(AccountProvider.GOOGLE, providerId);
        Account account;
        if (byProvider.isPresent()) {
            account = byProvider.get();
        } else {
            var byEmail = accountRepository.findByEmail(email);
            if (byEmail.isPresent() && byEmail.get().provider() != AccountProvider.GOOGLE) {
                throw new OAuth2AuthenticationException(
                        new OAuth2Error("ACCOUNT_EXISTS_DIFFERENT_PROVIDER",
                                "Ya existe una cuenta con email/contraseña — inicia sesión con tu contraseña primero",
                                null));
            }
            account = byEmail.orElseGet(() -> accountRepository.save(new Account(
                    null, email, null, AccountProvider.GOOGLE, providerId,
                    true, 0, null, null
            )));
        }

        return new DefaultOAuth2User(
                new AccountPrincipal(account.id(), account.email()).getAuthorities(),
                Map.of("sub", providerId, "email", email, "accountId", account.id()),
                "sub"
        );
    }
}
