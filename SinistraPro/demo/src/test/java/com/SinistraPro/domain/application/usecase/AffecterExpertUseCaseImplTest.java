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

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AffecterExpertUseCaseImplTest {

    @Mock private SinistreRepositoryPort sinistreRepository;
    @Mock private UtilisateurRepositoryPort utilisateurRepository;
    @Mock private HistoriqueRepositoryPort historiqueRepository;
    @Mock private NotificationService notificationService;

    @InjectMocks
    private AffecterExpertUseCaseImpl useCase;

    private Sinistre sinistre;
    private Utilisateur expert;
    private Utilisateur agent;
    private Utilisateur client;

    @BeforeEach
    void setUp() {
        client = new Utilisateur();
        client.setId(1L);
        client.setNom("Doe");
        client.setRole(Role.CLIENT);

        agent = new Utilisateur();
        agent.setId(2L);
        agent.setNom("Agent");
        agent.setRole(Role.AGENT);

        expert = new Utilisateur();
        expert.setId(3L);
        expert.setNom("Martin");
        expert.setRole(Role.EXPERT);

        sinistre = new Sinistre();
        sinistre.setId(10L);
        sinistre.setNumero("SIN-2026-001");
        sinistre.setStatut(StatutSinistre.DECLARE);
        sinistre.setClient(client);
    }



    @Test
    void affecter_expertValide_sinistreCorrectementMisAJour() {
        when(sinistreRepository.findById(10L)).thenReturn(Optional.of(sinistre));
        when(utilisateurRepository.findById(3L)).thenReturn(Optional.of(expert));
        when(sinistreRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Sinistre result = useCase.affecter(10L, 3L, "Affectation initiale", agent);


        assertThat(result).isNotNull();
        assertThat(result.getExpert()).isEqualTo(expert);
        assertThat(result.getAgent()).isEqualTo(agent);
        assertThat(result.getStatut()).isEqualTo(StatutSinistre.AFFECTE);
    }

    @Test
    void affecter_expertValide_sinistreEstSauvegarde() {
        when(sinistreRepository.findById(10L)).thenReturn(Optional.of(sinistre));
        when(utilisateurRepository.findById(3L)).thenReturn(Optional.of(expert));
        when(sinistreRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        useCase.affecter(10L, 3L, "Commentaire", agent);

        verify(sinistreRepository, times(1)).save(sinistre);
    }



    @Test
    void affecter_sinistreInexistant_leveIllegalArgumentException() {
        when(sinistreRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.affecter(99L, 3L, "Commentaire", agent))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Sinistre introuvable ID: 99");

        verify(sinistreRepository, never()).save(any());
        verify(notificationService, never()).creer(any(), any(), any(), any());
    }



    @Test
    void affecter_expertInexistant_leveIllegalArgumentException() {
        when(sinistreRepository.findById(10L)).thenReturn(Optional.of(sinistre));
        when(utilisateurRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.affecter(10L, 99L, "Commentaire", agent))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Expert introuvable ID: 99");

        verify(sinistreRepository, never()).save(any());
    }



    @Test
    void affecter_utilisateurNonExpert_leveIllegalStateException() {
        Utilisateur nonExpert = new Utilisateur();
        nonExpert.setId(5L);
        nonExpert.setNom("Dupont");
        nonExpert.setRole(Role.CLIENT);

        when(sinistreRepository.findById(10L)).thenReturn(Optional.of(sinistre));
        when(utilisateurRepository.findById(5L)).thenReturn(Optional.of(nonExpert));

        assertThatThrownBy(() -> useCase.affecter(10L, 5L, "Commentaire", agent))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("n'est pas un expert qualifié");

        verify(sinistreRepository, never()).save(any());
    }



    @Test
    void affecter_expertValide_historiqueEstSauvegarde() {
        // GIVEN
        sinistre.setStatut(StatutSinistre.DECLARE);
        when(sinistreRepository.findById(10L)).thenReturn(Optional.of(sinistre));
        when(utilisateurRepository.findById(3L)).thenReturn(Optional.of(expert));
        when(sinistreRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        useCase.affecter(10L, 3L, "Affectation expert Martin", agent);

        ArgumentCaptor<Historique> captor = ArgumentCaptor.forClass(Historique.class);
        verify(historiqueRepository, times(1)).save(captor.capture());

        Historique historique = captor.getValue();
        assertThat(historique.getSinistreId()).isEqualTo(10L);
        assertThat(historique.getNouveauStatut()).isEqualTo(StatutSinistre.AFFECTE);
        assertThat(historique.getCommentaire()).isEqualTo("Affectation expert Martin");
        assertThat(historique.getEffectuePar()).isEqualTo(agent);
        assertThat(historique.getDateAction()).isNotNull();
    }

    @Test
    void affecter_expertValide_ancienStatutConserve() {
        sinistre.setStatut(StatutSinistre.DECLARE);
        when(sinistreRepository.findById(10L)).thenReturn(Optional.of(sinistre));
        when(utilisateurRepository.findById(3L)).thenReturn(Optional.of(expert));
        when(sinistreRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        useCase.affecter(10L, 3L, "Commentaire", agent);

        ArgumentCaptor<Historique> captor = ArgumentCaptor.forClass(Historique.class);
        verify(historiqueRepository).save(captor.capture());
        assertThat(captor.getValue().getAncienStatut()).isEqualTo(StatutSinistre.DECLARE);
    }



    @Test
    void affecter_expertValide_notificationExpertEnvoyee() {
        when(sinistreRepository.findById(10L)).thenReturn(Optional.of(sinistre));
        when(utilisateurRepository.findById(3L)).thenReturn(Optional.of(expert));
        when(sinistreRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        useCase.affecter(10L, 3L, "Commentaire", agent);

        verify(notificationService).creer(
                eq(expert), any(), contains("SIN-2026-001"), eq("EXPERTISE")
        );
    }

    @Test
    void affecter_avecClient_notificationClientEnvoyee() {
        when(sinistreRepository.findById(10L)).thenReturn(Optional.of(sinistre));
        when(utilisateurRepository.findById(3L)).thenReturn(Optional.of(expert));
        when(sinistreRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        useCase.affecter(10L, 3L, "Commentaire", agent);

        verify(notificationService).creer(
                eq(client), any(), contains("Martin"), eq("STATUT_CHANGE")
        );
    }

    @Test
    void affecter_avecClient_deuxNotificationsEnvoyees() {
        when(sinistreRepository.findById(10L)).thenReturn(Optional.of(sinistre));
        when(utilisateurRepository.findById(3L)).thenReturn(Optional.of(expert));
        when(sinistreRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        useCase.affecter(10L, 3L, "Commentaire", agent);

        verify(notificationService, times(2)).creer(any(), any(), any(), any());
    }

    @Test
    void affecter_sansClient_seulementNotificationExpertEnvoyee() {
        sinistre.setClient(null);
        when(sinistreRepository.findById(10L)).thenReturn(Optional.of(sinistre));
        when(utilisateurRepository.findById(3L)).thenReturn(Optional.of(expert));
        when(sinistreRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        useCase.affecter(10L, 3L, "Commentaire", agent);

        verify(notificationService, times(1)).creer(any(), any(), any(), any());
        verify(notificationService).creer(eq(expert), any(), any(), eq("EXPERTISE"));
    }
}
