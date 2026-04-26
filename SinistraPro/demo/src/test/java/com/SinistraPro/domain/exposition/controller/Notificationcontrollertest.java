package com.SinistraPro.domain.exposition.controller;

import com.SinistraPro.domain.application.service.NotificationService;
import com.SinistraPro.domain.exposition.dto.response.NotificationResponse;
import com.SinistraPro.domain.model.Notification;
import com.SinistraPro.domain.model.Sinistre;
import com.SinistraPro.domain.model.Utilisateur;
import com.SinistraPro.domain.port.out.UtilisateurRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationControllerTest {

    @Mock private NotificationService notificationService;
    @Mock private UtilisateurRepositoryPort utilisateurRepository;
    @Mock private Authentication auth;

    @InjectMocks
    private NotificationController notificationController;

    private Utilisateur utilisateur;
    private Notification notif1;
    private Notification notif2;

    @BeforeEach
    void setUp() {
        utilisateur = new Utilisateur();
        utilisateur.setId(1L);
        utilisateur.setEmail("client@test.com");

        Sinistre sinistre = new Sinistre();
        sinistre.setId(10L);

        notif1 = Notification.builder()
                .id(1L)
                .message("Votre sinistre a été déclaré")
                .type("STATUT_CHANGE")
                .lue(false)
                .dateCreation(LocalDateTime.now())
                .sinistre(sinistre)
                .utilisateur(utilisateur)
                .build();

        notif2 = Notification.builder()
                .id(2L)
                .message("Un expert a été affecté")
                .type("EXPERTISE")
                .lue(true)
                .dateCreation(LocalDateTime.now())
                .sinistre(sinistre)
                .utilisateur(utilisateur)
                .build();

        when(auth.getName()).thenReturn("client@test.com");
        when(utilisateurRepository.findByEmail("client@test.com")).thenReturn(Optional.of(utilisateur));
    }

    @Test
    void getMesNotifications_utilisateurConnecte_retourne200AvecListe() {
        when(notificationService.getMesNotifications(1L)).thenReturn(List.of(notif1, notif2));

        ResponseEntity<List<NotificationResponse>> response = notificationController.getMesNotifications(auth);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(2);
        assertThat(response.getBody().get(0).getMessage()).isEqualTo("Votre sinistre a été déclaré");
        assertThat(response.getBody().get(0).getType()).isEqualTo("STATUT_CHANGE");
        assertThat(response.getBody().get(0).isLue()).isFalse();
        assertThat(response.getBody().get(0).getSinistreId()).isEqualTo(10L);
    }



    @Test
    void countNonLues_utilisateurConnecte_retourneCount() {
        when(notificationService.compterNonLues(1L)).thenReturn(3L);

        ResponseEntity<Map<String, Long>> response = notificationController.countNonLues(auth);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsEntry("count", 3L);
    }

    @Test
    void countNonLues_aucuneNonLue_retourneZero() {
        when(notificationService.compterNonLues(1L)).thenReturn(0L);

        ResponseEntity<Map<String, Long>> response = notificationController.countNonLues(auth);

        assertThat(response.getBody()).containsEntry("count", 0L);
    }



    @Test
    void marquerToutesLues_utilisateurConnecte_retourne200() {
        doNothing().when(notificationService).marquerToutesCommeLues(1L);

        ResponseEntity<Void> response = notificationController.marquerToutesLues(auth);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(notificationService, times(1)).marquerToutesCommeLues(1L);
    }

    @Test
    void getMesNotifications_notifSansSinistre_sinistreIdEstNull() {
        Notification notifSansSinistre = Notification.builder()
                .id(3L)
                .message("Notification sans sinistre")
                .type("STATUT_CHANGE")
                .lue(false)
                .dateCreation(LocalDateTime.now())
                .sinistre(null)
                .build();

        when(notificationService.getMesNotifications(1L)).thenReturn(List.of(notifSansSinistre));

        ResponseEntity<List<NotificationResponse>> response = notificationController.getMesNotifications(auth);

        assertThat(response.getBody().get(0).getSinistreId()).isNull();
    }
}