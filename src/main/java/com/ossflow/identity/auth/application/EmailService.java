package com.ossflow.identity.auth.application;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final String frontendUrl;

    public EmailService(JavaMailSender mailSender,
                        @Value("${app.frontend-url:http://localhost:5173}") String frontendUrl) {
        this.mailSender = mailSender;
        this.frontendUrl = frontendUrl;
    }

    public void sendVerificationEmail(String to, String rawToken) {
        send(to, verificationSubject(), verificationBody(rawToken));
    }

    public void sendPasswordResetEmail(String to, String rawToken) {
        send(to, passwordResetSubject(), passwordResetBody(rawToken));
    }

    public String verificationSubject() {
        return "Verifica tu correo en OssFlow";
    }

    public String passwordResetSubject() {
        return "Restablecer contraseña — OssFlow";
    }

    public String verificationBody(String rawToken) {
        String link = frontendUrl + "/verify-email?token=" + rawToken;
        return """
                <html><body style="font-family: sans-serif; background: #0a0a0a; color: #e5e5e5; padding: 40px;">
                  <h2 style="color: #a78bfa;">Verifica tu correo en OssFlow</h2>
                  <p>Haz clic en el botón para verificar tu cuenta:</p>
                  <a href="%s" style="display:inline-block;padding:12px 24px;background:#7c3aed;color:white;text-decoration:none;font-weight:bold;">
                    Verificar correo
                  </a>
                  <p style="color:#666;margin-top:24px;">Este enlace expira en 24 horas. Si no creaste una cuenta en OssFlow, ignora este correo.</p>
                </body></html>
                """.formatted(link);
    }

    public String passwordResetBody(String rawToken) {
        String link = frontendUrl + "/reset-password?token=" + rawToken;
        return """
                <html><body style="font-family: sans-serif; background: #0a0a0a; color: #e5e5e5; padding: 40px;">
                  <h2 style="color: #a78bfa;">Restablecer contraseña — OssFlow</h2>
                  <p>Haz clic en el botón para restablecer tu contraseña:</p>
                  <a href="%s" style="display:inline-block;padding:12px 24px;background:#7c3aed;color:white;text-decoration:none;font-weight:bold;">
                    Restablecer contraseña
                  </a>
                  <p style="color:#666;margin-top:24px;">Este enlace expira en 1 hora. Si no solicitaste esto, ignora este correo.</p>
                </body></html>
                """.formatted(link);
    }

    public void send(String to, String subject, String html) {
        try {
            var message = mailSender.createMimeMessage();
            var helper = new MimeMessageHelper(message, false, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setFrom("onboarding@resend.dev");
            helper.setText(html, true);
            mailSender.send(message);
        } catch (Exception e) {
            // Plan B3: log + propagar como runtime. El caller (register/forgot) puede
            // capturarla para decidir si devolver 201 igualmente (anti-enumeración) y
            // dejar al usuario reintentar manualmente via resend-verification.
            log.error("Failed to send email to {}: {}", to, e.getMessage(), e);
            throw new EmailDeliveryException("Email delivery failed", e);
        }
    }
}
