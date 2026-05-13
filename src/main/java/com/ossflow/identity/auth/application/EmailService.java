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

    // ── Send helpers ────────────────────────────────────────────────────────

    public void sendVerificationEmail(String to, String rawToken) {
        send(to, verificationSubject(), verificationBody(rawToken));
    }

    public void sendPasswordResetEmail(String to, String rawToken) {
        send(to, passwordResetSubject(), passwordResetBody(rawToken));
    }

    // ── Subjects ────────────────────────────────────────────────────────────

    public String verificationSubject() {
        return "Verifica tu correo en OssFlow";
    }

    public String passwordResetSubject() {
        return "Restablecer contraseña — OssFlow";
    }

    public String athleteJoinedSubject() { return "Nuevo alumno en OssFlow"; }

    public String athleteLeftSubject() { return "Un alumno se ha desvinculado — OssFlow"; }

    public String coachRemovedYouSubject() { return "Tu maestro te ha desvinculado — OssFlow"; }

    // ── Bodies ──────────────────────────────────────────────────────────────

    public String verificationBody(String rawToken) {
        String link = frontendUrl + "/verify-email?token=" + rawToken;
        String body = """
                <div class="badge">Verificación de cuenta</div>
                <div class="card-title">Confirma tu dirección de correo</div>
                <div class="card-body">
                  Gracias por unirte a OssFlow. Haz clic en el botón para verificar tu correo
                  y empezar a registrar tu progreso en Brazilian Jiu-Jitsu.
                </div>
                <a href="%s" class="btn">Verificar correo</a>
                <hr class="divider">
                <div class="footer">
                  Este enlace expira en <strong style="color:#f0ebe3;">24 horas</strong>.
                  Si no creaste una cuenta en OssFlow, ignora este correo.
                </div>
                """.formatted(link);
        return baseTemplate("Verifica tu correo — OssFlow", body);
    }

    public String passwordResetBody(String rawToken) {
        String link = frontendUrl + "/reset-password?token=" + rawToken;
        String body = """
                <div class="badge">Seguridad</div>
                <div class="card-title">Restablecer contraseña</div>
                <div class="card-body">
                  Recibimos una solicitud para restablecer la contraseña de tu cuenta en OssFlow.
                  Si fuiste tú, haz clic en el botón. Si no, ignora este correo — tu cuenta está segura.
                </div>
                <a href="%s" class="btn">Restablecer contraseña</a>
                <hr class="divider">
                <div class="footer">
                  Este enlace expira en <strong style="color:#f0ebe3;">1 hora</strong>.
                </div>
                """.formatted(link);
        return baseTemplate("Restablecer contraseña — OssFlow", body);
    }

    public String athleteJoinedBody(String athleteDisplayName) {
        String body = """
                <div class="badge">Nuevo alumno</div>
                <div class="card-title">%s se ha unido a tu gimnasio</div>
                <div class="card-body">
                  Un atleta ha redimido tu código de invitación y ahora forma parte de tu lista de alumnos en OssFlow.
                  Puedes ver su ficha desde el panel de coaching.
                </div>
                <hr class="divider">
                <div class="footer">Entra en OssFlow para ver la ficha de tu nuevo alumno.</div>
                """.formatted(athleteDisplayName);
        return baseTemplate("Nuevo alumno — OssFlow", body);
    }

    public String athleteLeftBody(String athleteDisplayName) {
        String body = """
                <div class="badge">Cambio en tu gimnasio</div>
                <div class="card-title">%s se ha desvinculado</div>
                <div class="card-body">
                  El atleta ha decidido desvincular su cuenta de tu gimnasio en OssFlow.
                  Ya no tendrás acceso a su ficha.
                </div>
                """.formatted(athleteDisplayName);
        return baseTemplate("Alumno desvinculado — OssFlow", body);
    }

    public String coachRemovedYouBody(String coachDisplayName) {
        String body = """
                <div class="badge">Cambio en tu cuenta</div>
                <div class="card-title">%s ha cancelado tu vinculación</div>
                <div class="card-body">
                  Tu maestro ha desvinculado tu cuenta de su gimnasio en OssFlow.
                  Tus datos de entrenamiento permanecen intactos y son completamente privados.
                  Puedes vincularte a otro maestro cuando quieras.
                </div>
                """.formatted(coachDisplayName);
        return baseTemplate("Desvinculado del gimnasio — OssFlow", body);
    }

    // ── Infrastructure ───────────────────────────────────────────────────────

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

    // ── Private ──────────────────────────────────────────────────────────────

    private String baseTemplate(String title, String bodyContent) {
        return """
                <!DOCTYPE html>
                <html lang="es">
                <head>
                  <meta charset="UTF-8">
                  <meta name="viewport" content="width=device-width, initial-scale=1.0">
                  <title>%s</title>
                  <style>
                    @import url('https://fonts.googleapis.com/css2?family=Playfair+Display:wght@700;900&family=Inter:wght@400;500;600&display=swap');
                    * { margin: 0; padding: 0; box-sizing: border-box; }
                    body {
                      background-color: #0f0f0f;
                      color: #f0ebe3;
                      font-family: 'Inter', sans-serif;
                      background-image: radial-gradient(circle, #2a2a2a 1px, transparent 1px);
                      background-size: 28px 28px;
                    }
                    .wrapper { max-width: 560px; margin: 0 auto; padding: 48px 24px; }
                    .logo-row { display: flex; align-items: center; gap: 10px; margin-bottom: 36px; }
                    .logo-text { font-family: 'Playfair Display', serif; font-weight: 900; font-size: 22px; color: #f0ebe3; letter-spacing: -0.5px; }
                    .card { background: #111111; border: 1px solid #2a2a2a; padding: 36px 32px; }
                    .card-title { font-family: 'Playfair Display', serif; font-weight: 700; font-size: 22px; color: #f0ebe3; margin-bottom: 14px; line-height: 1.3; }
                    .card-body { font-size: 14px; color: #888; line-height: 1.7; margin-bottom: 28px; font-family: 'Inter', monospace; }
                    .btn { display: inline-block; padding: 13px 28px; background: #f0ebe3; color: #0f0f0f; text-decoration: none; font-family: 'Inter', monospace; font-weight: 600; font-size: 11px; letter-spacing: 0.1em; text-transform: uppercase; }
                    .divider { border: none; border-top: 1px solid #2a2a2a; margin: 28px 0; }
                    .footer { font-size: 12px; color: #444; line-height: 1.6; margin-top: 28px; font-family: 'Inter', monospace; }
                    .badge { display: inline-block; border: 1px solid #2a2a2a; color: #666; font-size: 10px; font-weight: 600; padding: 3px 10px; margin-bottom: 20px; font-family: 'Inter', monospace; text-transform: uppercase; letter-spacing: 0.1em; }
                  </style>
                </head>
                <body>
                  <div class="wrapper">
                    <div class="logo-row">
                      <svg width="24" height="24" viewBox="0 0 80 80" fill="none" xmlns="http://www.w3.org/2000/svg">
                        <rect x="6" y="29" width="68" height="22" fill="#f0ebe3"/>
                        <rect x="34" y="19" width="12" height="42" fill="#f0ebe3"/>
                        <rect x="37" y="22" width="6" height="36" fill="#0f0f0f"/>
                        <rect x="6" y="39" width="28" height="3" fill="#0f0f0f"/>
                        <rect x="46" y="39" width="28" height="3" fill="#0f0f0f"/>
                        <rect x="30" y="51" width="8" height="13" fill="#f0ebe3"/>
                        <rect x="42" y="51" width="8" height="13" fill="#f0ebe3"/>
                      </svg>
                      <span class="logo-text">OssFlow</span>
                    </div>
                    <div class="card">
                      %s
                    </div>
                    <div class="footer">
                      <p>© 2026 OssFlow · Brazilian Jiu-Jitsu Knowledge System</p>
                      <p style="margin-top:6px;">Si no esperabas este correo, puedes ignorarlo de forma segura.</p>
                    </div>
                  </div>
                </body>
                </html>
                """.formatted(title, bodyContent);
    }
}
