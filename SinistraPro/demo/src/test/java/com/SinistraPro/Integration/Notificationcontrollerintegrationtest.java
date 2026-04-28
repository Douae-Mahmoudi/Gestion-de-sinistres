package com.SinistraPro.Integration;

import com.SinistraPro.domain.application.service.NotificationService;
import com.SinistraPro.domain.exposition.controller.NotificationController;
import com.SinistraPro.domain.infrastructure.security.JwtService;
import com.SinistraPro.domain.model.Notification;
import com.SinistraPro.domain.model.Role;
import com.SinistraPro.domain.model.Sinistre;
import com.SinistraPro.domain.model.Utilisateur;
import com.SinistraPro.domain.port.out.UtilisateurRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NotificationController.class)
@DisplayName("Tests d'intégration – NotificationController")
class NotificationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean private JwtService                jwtService;
    @MockBean private NotificationService       notificationService;
    @MockBean private UtilisateurRepositoryPort utilisateurRepository;

    private Utilisateur clientUser;
    private Sinistre    sinistre;
    private Notification notifNonLue;
    private Notification notifLue;

    @BeforeEach
    void setUp() {
        clientUser = Utilisateur.builder()
                .id(1L).nom("Dupont").prenom("Jean")
                .email("client@test.com").role(Role.CLIENT).build();

        sinistre = Sinistre.builder()
                .id(10L).numero("SP-010").build();

        notifNonLue = Notification.builder()
                .id(1L)
                .message("Votre sinistre a été déclaré")
                .type("STATUT_CHANGE")
                .lue(false)
                .dateCreation(LocalDateTime.now())
                .sinistre(sinistre)
                .utilisateur(clientUser)
                .build();

        notifLue = Notification.builder()
                .id(2L)
                .message("Un expert a été assigné")
                .type("EXPERTISE")
                .lue(true)
                .dateCreation(LocalDateTime.now().minusHours(2))
                .sinistre(sinistre)
                .utilisateur(clientUser)
                .build();
    }


    @Test
    @WithMockUser(username = "client@test.com", roles = "CLIENT")
    @DisplayName("GET /api/notifications – 200 OK avec la liste des notifications")
    void getMesNotifications_shouldReturn200_withList() throws Exception {

        when(utilisateurRepository.findByEmail("client@test.com"))
                .thenReturn(Optional.of(clientUser));
        when(notificationService.getMesNotifications(1L))
                .thenReturn(List.of(notifNonLue, notifLue));

        mockMvc.perform(get("/api/notifications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].message").value("Votre sinistre a été déclaré"))
                .andExpect(jsonPath("$[0].type").value("STATUT_CHANGE"))
                .andExpect(jsonPath("$[0].lue").value(false))
                .andExpect(jsonPath("$[0].sinistreId").value(10))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].lue").value(true));
    }

    @Test
    @WithMockUser(username = "client@test.com", roles = "CLIENT")
    @DisplayName("GET /api/notifications – 200 OK avec liste vide")
    void getMesNotifications_shouldReturn200_withEmptyList() throws Exception {

        when(utilisateurRepository.findByEmail("client@test.com"))
                .thenReturn(Optional.of(clientUser));
        when(notificationService.getMesNotifications(1L))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/notifications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("GET /api/notifications – 401/403 quand non authentifié")
    void getMesNotifications_shouldReturn4xx_whenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/notifications"))
                .andExpect(status().is4xxClientError());
    }


    @Test
    @WithMockUser(username = "client@test.com", roles = "CLIENT")
    @DisplayName("GET /api/notifications/non-lues/count – 200 OK avec le compteur")
    void countNonLues_shouldReturn200_withCount() throws Exception {

        when(utilisateurRepository.findByEmail("client@test.com"))
                .thenReturn(Optional.of(clientUser));
        when(notificationService.compterNonLues(1L)).thenReturn(3L);

        mockMvc.perform(get("/api/notifications/non-lues/count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(3));
    }

    @Test
    @WithMockUser(username = "client@test.com", roles = "CLIENT")
    @DisplayName("GET /api/notifications/non-lues/count – 200 OK quand aucune notification non lue")
    void countNonLues_shouldReturn200_withZero() throws Exception {

        when(utilisateurRepository.findByEmail("client@test.com"))
                .thenReturn(Optional.of(clientUser));
        when(notificationService.compterNonLues(1L)).thenReturn(0L);

        mockMvc.perform(get("/api/notifications/non-lues/count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(0));
    }



    @Test
    @WithMockUser(username = "client@test.com", roles = "CLIENT")
    @DisplayName("PUT /api/notifications/{id}/lire – 200 OK quand la notification est marquée lue")
    void marquerLue_shouldReturn200_whenValid() throws Exception {

        doNothing().when(notificationService).marquerCommeLue(1L);

        mockMvc.perform(put("/api/notifications/1/lire")
                        .with(csrf()))
                .andExpect(status().isOk());

        verify(notificationService, times(1)).marquerCommeLue(1L);
    }



    @Test
    @WithMockUser(username = "client@test.com", roles = "CLIENT")
    @DisplayName("PUT /api/notifications/lire-tout – 200 OK quand toutes marquées lues")
    void marquerToutesLues_shouldReturn200_whenValid() throws Exception {

        when(utilisateurRepository.findByEmail("client@test.com"))
                .thenReturn(Optional.of(clientUser));
        doNothing().when(notificationService).marquerToutesCommeLues(1L);

        mockMvc.perform(put("/api/notifications/lire-tout")
                        .with(csrf()))
                .andExpect(status().isOk());

        verify(notificationService, times(1)).marquerToutesCommeLues(1L);
    }
}