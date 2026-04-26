package com.SinistraPro.domain.application.service;

import com.SinistraPro.domain.port.out.UtilisateurRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final UtilisateurRepositoryPort utilisateurRepository;
    private final JavaMailSender mailSender;

    private final Map<String, CodeEntry> codeStore = new ConcurrentHashMap<>();

    public void demanderReset(String email) {
        utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Aucun compte associé à cet email."));

        String code = String.format("%06d", new Random().nextInt(999999));
        LocalDateTime expiration = LocalDateTime.now().plusMinutes(15);

        // Stocker le code
        codeStore.put(email, new CodeEntry(code, expiration));

        // Envoyer email propre
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Code de réinitialisation SinistraPro");
        message.setText(
                "Bonjour,\n\n" +
                        "Voici votre code de réinitialisation de mot de passe :\n\n" +
                        "━━━━━━━━━━━━\n" +
                        "   " + code + "\n" +
                        "━━━━━━━━━━━━\n\n" +
                        "Ce code est valable 15 minutes.\n" +
                        "Si vous n'avez pas fait cette demande, ignorez cet email.\n\n" +
                        "L'équipe SinistraPro"
        );
        mailSender.send(message);
    }

    public void verifierCode(String email, String code) {
        CodeEntry entry = codeStore.get(email);

        if (entry == null) {
            throw new IllegalArgumentException("Aucune demande de réinitialisation pour cet email.");
        }
        if (LocalDateTime.now().isAfter(entry.expiration())) {
            codeStore.remove(email);
            throw new IllegalArgumentException("Le code a expiré. Veuillez refaire une demande.");
        }
        if (!entry.code().equals(code)) {
            throw new IllegalArgumentException("Code incorrect.");
        }
    }

    public void reinitialiserMotDePasse(String email, String code, String nouveauMotDePasse) {
        verifierCode(email, code);

        utilisateurRepository.findByEmail(email).ifPresent(user -> {
            utilisateurRepository.updatePassword(email, nouveauMotDePasse);
        });

        codeStore.remove(email);
    }

    private record CodeEntry(String code, LocalDateTime expiration) {}
}