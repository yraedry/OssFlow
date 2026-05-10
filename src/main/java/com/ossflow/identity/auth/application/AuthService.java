package com.ossflow.identity.auth.application;

import com.ossflow.identity.auth.application.port.AccountRepositoryPort;
import com.ossflow.identity.auth.application.port.EmailVerificationTokenRepositoryPort;
import com.ossflow.identity.auth.application.port.PasswordResetTokenRepositoryPort;
import com.ossflow.identity.auth.application.port.RefreshTokenRepositoryPort;
import com.ossflow.identity.auth.domain.*;
import com.ossflow.identity.auth.infrastructure.web.dto.*;
import com.ossflow.shared.exception.BadRequestException;
import com.ossflow.shared.exception.NotFoundException;
import com.ossflow.shared.exception.UnprocessableException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;

@Slf4j
@Service
public class AuthService {

    private static final int BCRYPT_STRENGTH = 12;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final AccountRepositoryPort accountRepository;
    private final RefreshTokenRepositoryPort refreshTokenRepository;
    private final EmailVerificationTokenRepositoryPort emailVerificationTokenRepository;
    private final PasswordResetTokenRepositoryPort passwordResetTokenRepository;
    private final JwtService jwtService;
    private final EmailService emailService;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthService(AccountRepositoryPort accountRepository,
                       RefreshTokenRepositoryPort refreshTokenRepository,
                       EmailVerificationTokenRepositoryPort emailVerificationTokenRepository,
                       PasswordResetTokenRepositoryPort passwordResetTokenRepository,
                       JwtService jwtService,
                       EmailService emailService) {
        this.accountRepository = accountRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.emailVerificationTokenRepository = emailVerificationTokenRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.jwtService = jwtService;
        this.emailService = emailService;
        this.passwordEncoder = new BCryptPasswordEncoder(BCRYPT_STRENGTH);
    }

    @Transactional
    public void register(RegisterRequest request) {
        if (accountRepository.findByEmail(request.email()).isPresent()) {
            throw new BadRequestException("EMAIL_ALREADY_REGISTERED", "El correo ya está registrado");
        }
        String hash = passwordEncoder.encode(request.password());
        Account account = accountRepository.save(new Account(
                null, request.email(), hash, AccountProvider.LOCAL,
                null, false, 0, null, null
        ));
        String rawToken = generateToken();
        emailVerificationTokenRepository.save(new EmailVerificationToken(
                null, account.id(), sha256(rawToken),
                Instant.now().plusSeconds(86400), null
        ));
        emailService.sendVerificationEmail(account.email(), rawToken);
    }

    @Transactional
    public LoginResult login(LoginRequest request) {
        Account account = accountRepository.findByEmail(request.email())
                .filter(a -> a.passwordHash() != null && passwordEncoder.matches(request.password(), a.passwordHash()))
                .orElseThrow(() -> new UnprocessableException("INVALID_CREDENTIALS", "Credenciales inválidas"));

        if (!account.emailVerified()) {
            throw new UnprocessableException("EMAIL_NOT_VERIFIED", "Debes verificar tu correo antes de iniciar sesión");
        }

        String accessToken = jwtService.issueAccessToken(account);
        String rawRefreshToken = generateToken();
        refreshTokenRepository.save(new RefreshToken(
                null, account.id(), sha256(rawRefreshToken), account.tokenVersion(),
                Instant.now().plusSeconds(604800), Instant.now(), null
        ));
        return new LoginResult(accessToken, rawRefreshToken, account);
    }

    @Transactional
    public void logout(String rawRefreshToken) {
        String hash = sha256(rawRefreshToken);
        refreshTokenRepository.findByTokenHash(hash).ifPresent(rt ->
                refreshTokenRepository.revokeByAccountId(rt.accountId())
        );
    }

    @Transactional
    public RefreshResult refresh(String rawRefreshToken) {
        String hash = sha256(rawRefreshToken);
        RefreshToken stored = refreshTokenRepository.findByTokenHash(hash)
                .orElseThrow(() -> new UnprocessableException("INVALID_REFRESH_TOKEN", "Token inválido"));

        if (stored.revokedAt() != null || stored.expiresAt().isBefore(Instant.now())) {
            throw new UnprocessableException("INVALID_REFRESH_TOKEN", "Token expirado o revocado");
        }

        Account account = accountRepository.findById(stored.accountId())
                .orElseThrow(() -> new NotFoundException("ACCOUNT_NOT_FOUND", "Cuenta no encontrada"));

        if (stored.tokenVersion() != account.tokenVersion()) {
            throw new UnprocessableException("INVALID_REFRESH_TOKEN", "Token invalidado");
        }

        // Rotate: revoke old, issue new
        refreshTokenRepository.revokeByAccountId(account.id());
        String newRawToken = generateToken();
        refreshTokenRepository.save(new RefreshToken(
                null, account.id(), sha256(newRawToken), account.tokenVersion(),
                Instant.now().plusSeconds(604800), Instant.now(), null
        ));

        String accessToken = jwtService.issueAccessToken(account);
        return new RefreshResult(accessToken, newRawToken);
    }

    @Transactional
    public void verifyEmail(String rawToken) {
        String hash = sha256(rawToken);
        EmailVerificationToken token = emailVerificationTokenRepository.findByTokenHash(hash)
                .orElseThrow(() -> new BadRequestException("INVALID_TOKEN", "Token inválido o expirado"));

        if (token.usedAt() != null || token.expiresAt().isBefore(Instant.now())) {
            throw new BadRequestException("INVALID_TOKEN", "Token inválido o expirado");
        }

        Account account = accountRepository.findById(token.accountId())
                .orElseThrow(() -> new NotFoundException("ACCOUNT_NOT_FOUND", "Cuenta no encontrada"));
        Account verified = new Account(account.id(), account.email(), account.passwordHash(),
                account.provider(), account.providerId(), true, account.tokenVersion(),
                account.createdAt(), account.updatedAt());
        accountRepository.save(verified);

        // Mark token as used
        emailVerificationTokenRepository.save(new EmailVerificationToken(
                token.id(), token.accountId(), token.tokenHash(), token.expiresAt(), Instant.now()
        ));
    }

    @Transactional
    public void resendVerification(String email) {
        // Anti-enumeration: always 200, only send if account exists and unverified
        accountRepository.findByEmail(email)
                .filter(a -> !a.emailVerified())
                .ifPresent(account -> {
                    emailVerificationTokenRepository.deleteByAccountId(account.id());
                    String rawToken = generateToken();
                    emailVerificationTokenRepository.save(new EmailVerificationToken(
                            null, account.id(), sha256(rawToken),
                            Instant.now().plusSeconds(86400), null
                    ));
                    emailService.sendVerificationEmail(account.email(), rawToken);
                });
    }

    @Transactional
    public void forgotPassword(String email) {
        // Anti-enumeration: always 200
        accountRepository.findByEmail(email).ifPresent(account -> {
            String rawToken = generateToken();
            passwordResetTokenRepository.save(new PasswordResetToken(
                    null, account.id(), sha256(rawToken),
                    Instant.now().plusSeconds(3600), null
            ));
            emailService.sendPasswordResetEmail(account.email(), rawToken);
        });
    }

    @Transactional
    public void resetPassword(String rawToken, String newPassword) {
        String hash = sha256(rawToken);
        PasswordResetToken token = passwordResetTokenRepository.findByTokenHash(hash)
                .orElseThrow(() -> new BadRequestException("INVALID_TOKEN", "Token inválido o expirado"));

        if (token.usedAt() != null || token.expiresAt().isBefore(Instant.now())) {
            throw new BadRequestException("INVALID_TOKEN", "Token inválido o expirado");
        }

        Account account = accountRepository.findById(token.accountId())
                .orElseThrow(() -> new NotFoundException("ACCOUNT_NOT_FOUND", "Cuenta no encontrada"));

        String newHash = passwordEncoder.encode(newPassword);
        int newTokenVersion = account.tokenVersion() + 1;
        Account updated = new Account(account.id(), account.email(), newHash,
                account.provider(), account.providerId(), account.emailVerified(), newTokenVersion,
                account.createdAt(), account.updatedAt());
        accountRepository.save(updated);

        // Mark token as used
        passwordResetTokenRepository.save(new PasswordResetToken(
                token.id(), token.accountId(), token.tokenHash(), token.expiresAt(), Instant.now()
        ));

        // Revoke all refresh tokens (token_version bump invalidates them)
        refreshTokenRepository.revokeByAccountId(account.id());
    }

    private String generateToken() {
        byte[] bytes = new byte[32];
        SECURE_RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    public static String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (Exception e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }

    public record LoginResult(String accessToken, String rawRefreshToken, Account account) {}
    public record RefreshResult(String accessToken, String rawRefreshToken) {}
}
