package com.ossflow.identity.auth.application;

import com.ossflow.shared.properties.AppProperties;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OAuth2FailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private final String frontendUrl;

    public OAuth2FailureHandler(AppProperties appProperties) {
        this.frontendUrl = appProperties.frontendUrl();
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {
        String errorCode = mapError(exception);
        getRedirectStrategy().sendRedirect(request, response, frontendUrl + "/login?error=" + errorCode);
    }

    private String mapError(AuthenticationException exception) {
        if (exception instanceof OAuth2AuthenticationException oae) {
            String code = oae.getError().getErrorCode();
            return switch (code) {
                case "EMAIL_NOT_VERIFIED" -> "oauth_email_unverified";
                case "ACCOUNT_EXISTS_DIFFERENT_PROVIDER" -> "oauth_account_exists";
                default -> "oauth_failed";
            };
        }
        return "oauth_failed";
    }
}
