package com.SinistraPro.domain.application.service;

import com.SinistraPro.domain.model.Notification;
import com.SinistraPro.domain.model.Sinistre;
import com.SinistraPro.domain.model.Utilisateur;
import com.SinistraPro.domain.port.out.NotificationRepositoryPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepositoryPort notificationRepository;

    @InjectMocks
    private NotificationService notificationService;

    @Test
    @DisplayName("Devrait créer et sauvegarder une notification avec les bons paramètres")
    void creer_ShouldSaveNotification() {
        Utilisateur user = Utilisateur.builder().id(1L).build();
        Sinistre sinistre = Sinistre.builder().id(10L).build();
        String message = "Nouveau message";
        String type = "ALERTE";

        notificationService.creer(user, sinistre, message, type);

        ArgumentCaptor<Notification> notifCaptor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository, times(1)).save(notifCaptor.capture());

        Notification savedNotif = notifCaptor.getValue();
        assertThat(savedNotif.getMessage()).isEqualTo(message);
        assertThat(savedNotif.getUtilisateur()).isEqualTo(user);
        assertThat(savedNotif.isLue()).isFalse();
        assertThat(savedNotif.getDateCreation()).isNotNull();
    }

    @Test
    @DisplayName("Devrait retourner la liste des notifications d'un utilisateur")
    void getMesNotifications_ShouldReturnList() {
        Long userId = 1L;
        List<Notification> expectedNotifs = List.of(
                Notification.builder().id(1L).message("Notif 1").build(),
                Notification.builder().id(2L).message("Notif 2").build()
        );
        when(notificationRepository.findByUtilisateurId(userId)).thenReturn(expectedNotifs);

        List<Notification> result = notificationService.getMesNotifications(userId);

        assertThat(result).hasSize(2);
        assertThat(result).isEqualTo(expectedNotifs);
        verify(notificationRepository).findByUtilisateurId(userId);
    }

    @Test
    @DisplayName("Devrait compter les notifications non lues")
    void compterNonLues_ShouldReturnCount() {
        Long userId = 1L;
        when(notificationRepository.countNonLuesByUtilisateurId(userId)).thenReturn(5L);

        long count = notificationService.compterNonLues(userId);


        assertThat(count).isEqualTo(5L);
        verify(notificationRepository).countNonLuesByUtilisateurId(userId);
    }

    @Test
    @DisplayName("Devrait appeler le repository pour marquer une notification comme lue")
    void marquerCommeLue_ShouldInvokeRepository() {
        Long notifId = 100L;

        notificationService.marquerCommeLue(notifId);

        verify(notificationRepository).marquerCommeLue(notifId);
    }

    @Test
    @DisplayName("Devrait marquer toutes les notifications comme lues pour un utilisateur")
    void marquerToutesCommeLues_ShouldInvokeRepository() {
        Long userId = 1L;

        notificationService.marquerToutesCommeLues(userId);


        verify(notificationRepository).marquerToutesCommeLues(userId);
    }
}