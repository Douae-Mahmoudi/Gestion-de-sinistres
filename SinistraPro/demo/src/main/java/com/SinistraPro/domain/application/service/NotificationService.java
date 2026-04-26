package com.SinistraPro.domain.application.service;

import com.SinistraPro.domain.model.*;
import com.SinistraPro.domain.port.out.NotificationRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepositoryPort notificationRepository;

    public void creer(Utilisateur utilisateur, Sinistre sinistre, String message, String type) {
        Notification notif = Notification.builder()
                .utilisateur(utilisateur)
                .sinistre(sinistre)
                .message(message)
                .type(type)
                .lue(false)
                .dateCreation(LocalDateTime.now())
                .build();
        notificationRepository.save(notif);
    }

    public List<Notification> getMesNotifications(Long utilisateurId) {
        return notificationRepository.findByUtilisateurId(utilisateurId);
    }

    public long compterNonLues(Long utilisateurId) {
        return notificationRepository.countNonLuesByUtilisateurId(utilisateurId);
    }

    public void marquerCommeLue(Long notifId) {
        notificationRepository.marquerCommeLue(notifId);
    }

    public void marquerToutesCommeLues(Long utilisateurId) {
        notificationRepository.marquerToutesCommeLues(utilisateurId);
    }
}