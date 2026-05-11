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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;

@Service
public class AuthService {

    private static final int BCRYPT_STRENGTH = 12;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final AccountRepositoryPort accountRepository;
    private final RefreshTokenRepositoryPort refreshTokenRepository;
    private final EmailVerificationTokenRepositoryPort emailVerificationTokenRepository;
    private final PasswordResetTokenRepositoryPort passwordResetTokenRepository;
    private final JwtService jwtService;
    private final EmailOutboxService emailOutbox;
    private final AccountEventService accountEventService;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthService(AccountRepositoryPort accountRepository,
                       RefreshTokenRepositoryPort refreshTokenRepository,
                       EmailVerificationTokenRepositoryPort emailVerificationTokenRepository,
                       PasswordResetTokenRepositoryPort passwordResetTokenRepository,
                       JwtService jwtService,
                       EmailOutboxService emailOutbox,
                       AccountEventService accountEventService) {
        this.accountRepository = accountRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.emailVerificationTokenRepository = emailVerificationTokenRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.jwtService = jwtService;
        this.emailOutbox = emailOutbox;
        this.accountEventService = accountEventService;
        this.passwordEncoder = new BCryptPasswordEncoder(BCRYPT_STRENGTH);
    }

    @Transactional
    public void register(RegisterRequest request) {
        register(request, null, null);
    }

    @Transactional
    public void register(RegisterRequest request, String ip, String userAgent) {
        var existing = accountRepository.findByEmail(request.email());
        if (existing.isPresent()) {
            // Anti-enumeración: respuesta uniforme 201. Si la cuenta existe y NO está
            // verificada, reenvía verificación; si está verificada, no hace nada
            // (el usuario verá su email/login normal). Ejecutamos el hash bcrypt
            // igualmente para evitar timing attack basado en latencia.
            passwordEncoder.encode(request.password());
            Account account = existing.get();
            if (!account.emailVerified()) {
                emailVerificationTokenRepository.deleteByAccountId(account.id());
                String rawToken = generateToken();
                emailVerificationTokenRepository.save(new EmailVerificationToken(
                        null, account.id(), sha256(rawToken),
                        Instant.now().plusSeconds(86400), null
                ));
                emailOutbox.enqueueVerification(account.id(), account.email(), rawToken);
            }
            return;
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
        emailOutbox.enqueueVerification(account.id(), account.email(), rawToken);
        accountEventService.record(account.id(), AccountEventType.REGISTER, ip, userAgent);
    }

    @Transactional
    public LoginResult login(LoginRequest request) {
        return login(request, null, null);
    }

    // Dummy hash usada en timing-safe comparisons para cuentas inexistentes.
    // Calculada una sola vez con BCrypt strength 12. Evita que un atacante
    // distinga "email no existe" de "contraseña incorrecta" por latencia.
    private static final String DUMMY_HASH =
            "$2a$12$vI8aWBnW3fID.ZQ4/zo1G.q1lRps.9cGLcZEiGDMVr5yUP1KUjF0e";

    @Transactional
    public LoginResult login(LoginRequest request, String ip, String userAgent) {
        var accountOpt = accountRepository.findByEmail(request.email());

        // Anti-timing: siempre ejecutamos BCrypt, exista o no la cuenta.
        // Si no existe, comparamos contra un hash dummy para tener latencia uniforme.
        String hashToCheck = accountOpt
                .map(Account::passwordHash)
                .filter(h -> h != null)
                .orElse(DUMMY_HASH);
        boolean passwordMatches = passwordEncoder.matches(request.password(), hashToCheck);

        Account account = accountOpt
                .filter(a -> a.passwordHash() != null && passwordMatches)
                .orElseThrow(() -> {
                    // LOGIN_FAILED: intentamos registrar el evento si el email existe.
                    accountOpt.ifPresent(a ->
                            accountEventService.record(a.id(), AccountEventType.LOGIN_FAILED, ip, userAgent));
                    return new UnprocessableException("INVALID_CREDENTIALS", "Credenciales inválidas");
                });

        if (!account.emailVerified()) {
            accountEventService.record(account.id(), AccountEventType.LOGIN_FAILED, ip, userAgent);
            throw new UnprocessableException("EMAIL_NOT_VERIFIED", "Debes verificar tu correo antes de iniciar sesión");
        }

        String accessToken = jwtService.issueAccessToken(account);
        String rawRefreshToken = generateToken();
        refreshTokenRepository.save(new RefreshToken(
                null, account.id(), sha256(rawRefreshToken), account.tokenVersion(),
                Instant.now().plusSeconds(604800), Instant.now(), null
        ));
        accountEventService.record(account.id(), AccountEventType.LOGIN, ip, userAgent);
        return new LoginResult(accessToken, rawRefreshToken, account);
    }

    @Transactional
    public void logout(String rawRefreshToken) {
        logout(rawRefreshToken, null, null);
    }

    @Transactional
    public void logout(String rawRefreshToken, String ip, String userAgent) {
        if (rawRefreshToken == null || rawRefreshToken.isBlank()) {
            return;
        }
        String hash = sha256(rawRefreshToken);
        refreshTokenRepository.findByTokenHash(hash).ifPresent(rt -> {
            refreshTokenRepository.revokeByAccountId(rt.accountId());
            // Bumpea tokenVersion para invalidar access tokens vivos (15min).
            accountRepository.findById(rt.accountId()).ifPresent(account ->
                    accountRepository.save(new Account(account.id(), account.email(), account.passwordHash(),
                            account.provider(), account.providerId(), account.emailVerified(),
                            account.tokenVersion() + 1, account.createdAt(), account.updatedAt()))
            );
            accountEventService.record(rt.accountId(), AccountEventType.LOGOUT, ip, userAgent);
        });
    }

    // Ventana de gracia: dentro de este margen tras revocar, asumimos que un reuse del
    // mismo token revocado es double-click/retry de red del usuario legítimo y devolvemos
    // el token de reemplazo en lugar de invalidar la familia.
    private static final long GRACE_WINDOW_SECONDS = 5;

    @Transactional
    public RefreshResult refresh(String rawRefreshToken) {
        String hash = sha256(rawRefreshToken);
        RefreshToken stored = refreshTokenRepository.findByTokenHash(hash)
                .orElseThrow(() -> new UnprocessableException("INVALID_REFRESH_TOKEN", "Token inválido"));

        if (stored.expiresAt().isBefore(Instant.now())) {
            throw new UnprocessableException("INVALID_REFRESH_TOKEN", "Token expirado");
        }

        Account account = accountRepository.findById(stored.accountId())
                .orElseThrow(() -> new NotFoundException("ACCOUNT_NOT_FOUND", "Cuenta no encontrada"));

        if (stored.revokedAt() != null) {
            return handleRevokedToken(stored, account);
        }

        if (stored.tokenVersion() != account.tokenVersion()) {
            throw new UnprocessableException("INVALID_REFRESH_TOKEN", "Token invalidado");
        }

        return rotateToken(stored, account);
    }

    private RefreshResult handleRevokedToken(RefreshToken stored, Account account) {
        Instant now = Instant.now();
        boolean withinGrace = stored.revokedAt() != null
                && stored.revokedAt().isAfter(now.minusSeconds(GRACE_WINDOW_SECONDS));
        if (withinGrace && stored.replacedById() != null) {
            RefreshToken replacement = refreshTokenRepository.findById(stored.replacedById()).orElse(null);
            if (replacement != null && replacement.revokedAt() == null) {
                // Idempotente: re-emitimos un access token nuevo sobre la misma cadena;
                // el caller no recibe un raw refresh nuevo, debe usar el que ya tiene en cookie.
                String accessToken = jwtService.issueAccessToken(account);
                return new RefreshResult(accessToken, null);
            }
        }
        // Reuse detectado: invalida toda la familia + bumpea tokenVersion para tumbar access tokens vivos
        refreshTokenRepository.revokeByAccountId(account.id());
        accountRepository.save(new Account(account.id(), account.email(), account.passwordHash(),
                account.provider(), account.providerId(), account.emailVerified(),
                account.tokenVersion() + 1, account.createdAt(), account.updatedAt()));
        accountEventService.record(account.id(), AccountEventType.TOKEN_REUSE_DETECTED, null, null);
        throw new UnprocessableException("INVALID_REFRESH_TOKEN", "Token reuse detectado: sesión invalidada");
    }

    private RefreshResult rotateToken(RefreshToken stored, Account account) {
        String newRawToken = generateToken();
        RefreshToken newToken = refreshTokenRepository.save(new RefreshToken(
                null, account.id(), sha256(newRawToken), account.tokenVersion(),
                Instant.now().plusSeconds(604800), Instant.now(), null, null
        ));
        // Marcamos el viejo como revocado y apuntando al nuevo (chain para grace window)
        refreshTokenRepository.save(new RefreshToken(
                stored.id(), stored.accountId(), stored.tokenHash(), stored.tokenVersion(),
                stored.expiresAt(), stored.createdAt(), Instant.now(), newToken.id()
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
                    emailOutbox.enqueueVerification(account.id(), account.email(), rawToken);
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
            emailOutbox.enqueuePasswordReset(account.id(), account.email(), rawToken);
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
        accountEventService.record(account.id(), AccountEventType.PASSWORD_RESET, null, null);
    }

    @Transactional
    public void changePassword(Long accountId, String currentPassword, String newPassword) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new NotFoundException("ACCOUNT_NOT_FOUND", "Cuenta no encontrada"));
        if (account.passwordHash() == null || !passwordEncoder.matches(currentPassword, account.passwordHash())) {
            throw new BadRequestException("WRONG_PASSWORD", "Contraseña actual incorrecta");
        }
        String newHash = passwordEncoder.encode(newPassword);
        accountRepository.save(new Account(account.id(), account.email(), newHash,
                account.provider(), account.providerId(), account.emailVerified(),
                account.tokenVersion(), account.createdAt(), account.updatedAt()));
    }

    public Account findAccountById(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("ACCOUNT_NOT_FOUND", "Cuenta no encontrada"));
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
