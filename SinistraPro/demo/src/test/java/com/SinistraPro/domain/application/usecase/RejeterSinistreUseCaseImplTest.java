package com.SinistraPro.domain.application.usecase;

import com.SinistraPro.domain.model.*;
import com.SinistraPro.domain.port.out.DecisionRepositoryPort;
import com.SinistraPro.domain.port.out.HistoriqueRepositoryPort;
import com.SinistraPro.domain.port.out.SinistreRepositoryPort;
import com.SinistraPro.domain.port.out.UtilisateurRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RejeterSinistreUseCaseImplTest {

    @Mock private SinistreRepositoryPort sinistreRepository;
    @Mock private UtilisateurRepositoryPort utilisateurRepository;
    @Mock private DecisionRepositoryPort decisionRepository;
    @Mock private HistoriqueRepositoryPort historiqueRepository;

    @InjectMocks
    private RejeterSinistreUseCaseImpl rejeterSinistreUseCase;

    private Sinistre sinistre;
    private Utilisateur superviseur;

    @BeforeEach
    void setUp() {
        sinistre = Sinistre.builder()
                .id(1L)
                .statut(StatutSinistre.EVALUE)
                .build();

        superviseur = Utilisateur.builder()
                .id(100L)
                .role(Role.SUPERVISEUR)
                .build();
    }

    @Test
    @DisplayName("Succès : Rejet d'un sinistre avec motif par un superviseur")
    void rejeter_ShouldSucceed_WhenUserIsSuperviseur() {
        // Given
        String motif = "Dossier frauduleux";
        when(sinistreRepository.findById(1L)).thenReturn(Optional.of(sinistre));
        when(utilisateurRepository.findById(100L)).thenReturn(Optional.of(superviseur));
        when(decisionRepository.save(any(Decision.class))).thenAnswer(i -> i.getArguments()[0]);
        when(sinistreRepository.save(any(Sinistre.class))).thenAnswer(i -> i.getArguments()[0]);

        // When
        Sinistre result = rejeterSinistreUseCase.rejeter(1L, motif, 100L);

        // Then
        assertThat(result.getStatut()).isEqualTo(StatutSinistre.REJETE);
        assertThat(result.getDecision().getStatut()).isEqualTo(StatutDecision.REJETE);
        assertThat(result.getDecision().getMotif()).isEqualTo(motif);

        // Vérification de la traçabilité
        verify(decisionRepository).save(any(Decision.class));
        verify(historiqueRepository).save(argThat(h ->
                h.getNouveauStatut() == StatutSinistre.REJETE &&
                        h.getCommentaire().contains(motif)
        ));
        verify(sinistreRepository).save(any(Sinistre.class));
    }

    @Test
    @DisplayName("Échec : Un agent ne peut pas rejeter un sinistre")
    void rejeter_ShouldFail_WhenUserIsNotSuperviseur() {
        // Given
        Utilisateur agent = Utilisateur.builder().id(2L).role(Role.AGENT).build();
        when(sinistreRepository.findById(1L)).thenReturn(Optional.of(sinistre));
        when(utilisateurRepository.findById(2L)).thenReturn(Optional.of(agent));

        // When & Then
        assertThatThrownBy(() -> rejeterSinistreUseCase.rejeter(1L, "Motif", 2L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Seul un superviseur peut rejeter");

        verify(sinistreRepository, never()).save(any());
    }

    @Test
    @DisplayName("Échec : Sinistre introuvable")
    void rejeter_ShouldThrowException_WhenSinistreNotFound() {
        // Given
        when(sinistreRepository.findById(99L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> rejeterSinistreUseCase.rejeter(99L, "Motif", 100L))
                .isInstanceOf(IllegalArgumentException.class);
    }
}