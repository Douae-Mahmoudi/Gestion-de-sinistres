package com.SinistraPro.domain.application.usecase;

import com.SinistraPro.domain.application.service.NotificationService;
import com.SinistraPro.domain.model.*;
import com.SinistraPro.domain.port.out.HistoriqueRepositoryPort;
import com.SinistraPro.domain.port.out.SinistreRepositoryPort;
import com.SinistraPro.domain.port.out.UtilisateurRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeclarerSinistreUseCaseImplTest {

    @Mock private SinistreRepositoryPort sinistreRepository;
    @Mock private UtilisateurRepositoryPort utilisateurRepository;
    @Mock private HistoriqueRepositoryPort historiqueRepository;
    @Mock private NotificationService notificationService;

    @InjectMocks
    private DeclarerSinistreUseCaseImpl useCase;

    private Utilisateur client;
    private Utilisateur agent1;
    private Utilisateur agent2;
    private Sinistre sinistre;

    @BeforeEach
    void setUp() {
        client = new Utilisateur();
        client.setId(1L);
        client.setNom("Doe");
        client.setPrenom("John");
        client.setEmail("client@test.com");
        client.setRole(Role.CLIENT);

        agent1 = new Utilisateur();
        agent1.setId(2L);
        agent1.setRole(Role.AGENT);

        agent2 = new Utilisateur();
        agent2.setId(3L);
        agent2.setRole(Role.AGENT);

        sinistre = new Sinistre();
        sinistre.setClient(client);
        sinistre.setTypeSinistre("ACCIDENT");
        sinistre.setDescription("Collision sur autoroute");
        sinistre.setLieuIncident("Casablanca");
        sinistre.setDateIncident(LocalDate.of(2026, 4, 20));
    }



    @Test
    void declarer_clientValide_sinistreCorrectementInitialise() {
        Sinistre saved = new Sinistre();
        saved.setId(10L);
        saved.setClient(client);
        saved.setNumero("SIN-2026-0425101000");
        saved.setStatut(StatutSinistre.DECLARE);

        when(utilisateurRepository.findById(1L)).thenReturn(Optional.of(client));
        when(sinistreRepository.save(any())).thenReturn(saved);
        when(utilisateurRepository.findByRole(Role.AGENT)).thenReturn(List.of(agent1, agent2));

        Sinistre result = useCase.declarer(sinistre);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(10L);
        verify(sinistreRepository, times(1)).save(any(Sinistre.class));
    }

    @Test
    void declarer_clientValide_statutEstDECLARE() {
        when(utilisateurRepository.findById(1L)).thenReturn(Optional.of(client));
        when(sinistreRepository.save(any())).thenAnswer(inv -> {
            Sinistre s = inv.getArgument(0);
            s.setId(10L);
            return s;
        });
        when(utilisateurRepository.findByRole(Role.AGENT)).thenReturn(List.of());

        Sinistre result = useCase.declarer(sinistre);

        assertThat(result.getStatut()).isEqualTo(StatutSinistre.DECLARE);
    }

    @Test
    void declarer_clientValide_numeroEstGenere() {
        when(utilisateurRepository.findById(1L)).thenReturn(Optional.of(client));
        when(sinistreRepository.save(any())).thenAnswer(inv -> {
            Sinistre s = inv.getArgument(0);
            s.setId(10L);
            return s;
        });
        when(utilisateurRepository.findByRole(Role.AGENT)).thenReturn(List.of());

        Sinistre result = useCase.declarer(sinistre);

        assertThat(result.getNumero()).isNotNull();
        assertThat(result.getNumero()).startsWith("SIN-2026-");
    }

    @Test
    void declarer_clientValide_dateDeclarationEstDefinie() {
        when(utilisateurRepository.findById(1L)).thenReturn(Optional.of(client));
        when(sinistreRepository.save(any())).thenAnswer(inv -> {
            Sinistre s = inv.getArgument(0);
            s.setId(10L);
            return s;
        });
        when(utilisateurRepository.findByRole(Role.AGENT)).thenReturn(List.of());

        Sinistre result = useCase.declarer(sinistre);

        assertThat(result.getDateDeclaration()).isNotNull();
    }



    @Test
    void declarer_clientInexistant_leveIllegalArgumentException() {
        // GIVEN
        when(utilisateurRepository.findById(1L)).thenReturn(Optional.empty());

        // WHEN & THEN
        assertThatThrownBy(() -> useCase.declarer(sinistre))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Client introuvable");

        verify(sinistreRepository, never()).save(any());
    }


    @Test
    void declarer_utilisateurNonClient_leveIllegalStateException() {
        Utilisateur agent = new Utilisateur();
        agent.setId(1L);
        agent.setRole(Role.AGENT);

        when(utilisateurRepository.findById(1L)).thenReturn(Optional.of(agent));

        assertThatThrownBy(() -> useCase.declarer(sinistre))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Seul un client peut déclarer un sinistre");

        verify(sinistreRepository, never()).save(any());
    }



    @Test
    void declarer_clientValide_historiqueEstSauvegarde() {
        Sinistre saved = new Sinistre();
        saved.setId(10L);
        saved.setClient(client);
        saved.setStatut(StatutSinistre.DECLARE);

        when(utilisateurRepository.findById(1L)).thenReturn(Optional.of(client));
        when(sinistreRepository.save(any())).thenReturn(saved);
        when(utilisateurRepository.findByRole(Role.AGENT)).thenReturn(List.of());

        useCase.declarer(sinistre);

        ArgumentCaptor<Historique> captor = ArgumentCaptor.forClass(Historique.class);
        verify(historiqueRepository, times(1)).save(captor.capture());

        Historique historique = captor.getValue();
        assertThat(historique.getNouveauStatut()).isEqualTo(StatutSinistre.DECLARE);
        assertThat(historique.getCommentaire()).contains("déclaré");
        assertThat(historique.getEffectuePar()).isEqualTo(client);
    }



    @Test
    void declarer_clientValide_notificationClientEnvoyee() {
        // GIVEN
        Sinistre saved = new Sinistre();
        saved.setId(10L);
        saved.setClient(client);
        saved.setNumero("SIN-2026-001");
        saved.setStatut(StatutSinistre.DECLARE);

        when(utilisateurRepository.findById(1L)).thenReturn(Optional.of(client));
        when(sinistreRepository.save(any())).thenReturn(saved);
        when(utilisateurRepository.findByRole(Role.AGENT)).thenReturn(List.of());

        // WHEN
        useCase.declarer(sinistre);

        // THEN
        verify(notificationService, atLeastOnce()).creer(
                eq(client), eq(saved), contains("SIN-2026-001"), eq("STATUT_CHANGE")
        );
    }

    @Test
    void declarer_deuxAgents_deuxNotificationsAgentsEnvoyees() {
        // GIVEN
        Sinistre saved = new Sinistre();
        saved.setId(10L);
        saved.setClient(client);
        saved.setNumero("SIN-2026-001");
        saved.setStatut(StatutSinistre.DECLARE);

        when(utilisateurRepository.findById(1L)).thenReturn(Optional.of(client));
        when(sinistreRepository.save(any())).thenReturn(saved);
        when(utilisateurRepository.findByRole(Role.AGENT)).thenReturn(List.of(agent1, agent2));

        useCase.declarer(sinistre);

        verify(notificationService, times(3)).creer(any(), any(), any(), any());
        verify(notificationService).creer(eq(agent1), eq(saved), any(), eq("NOUVELLE_DECLARATION"));
        verify(notificationService).creer(eq(agent2), eq(saved), any(), eq("NOUVELLE_DECLARATION"));
    }

    @Test
    void declarer_aucunAgent_seulementNotificationClientEnvoyee() {
        Sinistre saved = new Sinistre();
        saved.setId(10L);
        saved.setClient(client);
        saved.setNumero("SIN-2026-001");
        saved.setStatut(StatutSinistre.DECLARE);

        when(utilisateurRepository.findById(1L)).thenReturn(Optional.of(client));
        when(sinistreRepository.save(any())).thenReturn(saved);
        when(utilisateurRepository.findByRole(Role.AGENT)).thenReturn(List.of());


        useCase.declarer(sinistre);

        verify(notificationService, times(1)).creer(any(), any(), any(), any());
        verify(notificationService).creer(eq(client), eq(saved), any(), eq("STATUT_CHANGE"));
    }
}