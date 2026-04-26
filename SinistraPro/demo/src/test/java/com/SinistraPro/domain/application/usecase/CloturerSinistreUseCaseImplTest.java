package com.SinistraPro.domain.application.usecase;

import com.SinistraPro.domain.model.*;
import com.SinistraPro.domain.port.out.DecisionRepositoryPort;
import com.SinistraPro.domain.port.out.HistoriqueRepositoryPort;
import com.SinistraPro.domain.port.out.SinistreRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CloturerSinistreUseCaseImplTest {

    @Mock private SinistreRepositoryPort sinistreRepository;
    @Mock private DecisionRepositoryPort decisionRepository;
    @Mock private HistoriqueRepositoryPort historiqueRepository;

    @InjectMocks
    private CloturerSinistreUseCaseImpl cloturerSinistreUseCase;

    private Sinistre sinistre;
    private Decision decisionApprouvee;
    private Utilisateur agent;

    @BeforeEach
    void setUp() {
        agent = Utilisateur.builder().id(1L).nom("Agent Test").build();

        decisionApprouvee = Decision.builder()
                .id(10L)
                .statut(StatutDecision.APPROUVE)
                .build();

        sinistre = Sinistre.builder()
                .id(100L)
                .statut(StatutSinistre.APPROUVE)
                .decision(decisionApprouvee)
                .agent(agent)
                .build();
    }

    @Test
    @DisplayName("Succès : Clôture du sinistre avec informations de virement")
    void cloturer_ShouldSucceed_WhenDecisionIsApproved() {
        String numVirement = "VIR-2026-XYZ";
        LocalDate datePaiement = LocalDate.now();

        when(sinistreRepository.findById(100L)).thenReturn(Optional.of(sinistre));
        when(decisionRepository.save(any(Decision.class))).thenReturn(decisionApprouvee);
        when(sinistreRepository.save(any(Sinistre.class))).thenAnswer(i -> i.getArguments()[0]);

        Sinistre result = cloturerSinistreUseCase.cloturer(100L, numVirement, datePaiement);

        assertThat(result.getStatut()).isEqualTo(StatutSinistre.CLOTURE);
        assertThat(result.getDecision().getNumeroVirement()).isEqualTo(numVirement);
        assertThat(result.getDecision().getDatePaiement()).isEqualTo(datePaiement);

        verify(historiqueRepository).save(argThat(h ->
                h.getNouveauStatut() == StatutSinistre.CLOTURE &&
                        h.getCommentaire().contains(numVirement)
        ));
        verify(sinistreRepository).save(any(Sinistre.class));
    }

    @Test
    @DisplayName("Échec : Impossible de clôturer si la décision est manquante")
    void cloturer_ShouldFail_WhenDecisionIsNull() {

        sinistre.setDecision(null);
        when(sinistreRepository.findById(100L)).thenReturn(Optional.of(sinistre));

        assertThatThrownBy(() -> cloturerSinistreUseCase.cloturer(100L, "V", LocalDate.now()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("sans décision approuvée");
    }

    @Test
    @DisplayName("Échec : Impossible de clôturer si la décision n'est pas approuvée")
    void cloturer_ShouldFail_WhenDecisionNotApproved() {
        decisionApprouvee.setStatut(StatutDecision.REJETE);
        when(sinistreRepository.findById(100L)).thenReturn(Optional.of(sinistre));

        assertThatThrownBy(() -> cloturerSinistreUseCase.cloturer(100L, "V", LocalDate.now()))
                .isInstanceOf(IllegalStateException.class);
    }
}