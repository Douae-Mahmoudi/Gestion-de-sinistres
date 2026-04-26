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

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApprouverSinistreUseCaseImplTest {

    @Mock private SinistreRepositoryPort sinistreRepository;
    @Mock private UtilisateurRepositoryPort utilisateurRepository;
    @Mock private DecisionRepositoryPort decisionRepository;
    @Mock private HistoriqueRepositoryPort historiqueRepository;

    @InjectMocks
    private ApprouverSinistreUseCaseImpl approuverSinistreUseCase;

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
    @DisplayName("Succès : Approbation standard (montant < 50 000)")
    void approuver_StandardAmount_ShouldSucceed() {
        BigDecimal montant = new BigDecimal("10000");
        when(sinistreRepository.findById(1L)).thenReturn(Optional.of(sinistre));
        when(utilisateurRepository.findById(100L)).thenReturn(Optional.of(superviseur));
        when(decisionRepository.save(any(Decision.class))).thenAnswer(i -> i.getArguments()[0]);
        when(sinistreRepository.save(any(Sinistre.class))).thenAnswer(i -> i.getArguments()[0]);

        Sinistre result = approuverSinistreUseCase.approuver(1L, montant, "OK", 100L);

        assertThat(result.getStatut()).isEqualTo(StatutSinistre.APPROUVE);
        verify(decisionRepository).save(any(Decision.class));
        verify(historiqueRepository).save(any(Historique.class));
    }

    @Test
    @DisplayName("Règle métier : Double validation requise (montant > 50 000)")
    void approuver_HighAmount_FirstValidation_ShouldStayInCurrentStatus() {
        BigDecimal montantFort = new BigDecimal("60000");
        when(sinistreRepository.findById(1L)).thenReturn(Optional.of(sinistre));
        when(utilisateurRepository.findById(100L)).thenReturn(Optional.of(superviseur));
        when(decisionRepository.findBySinistreId(1L)).thenReturn(Optional.empty()); // Première fois

        Sinistre result = approuverSinistreUseCase.approuver(1L, montantFort, "Gros sinistre", 100L);


        assertThat(result.getStatut()).isEqualTo(StatutSinistre.EVALUE);

        verify(decisionRepository).save(argThat(d -> d.getMotif().contains("EN ATTENTE DOUBLE VALIDATION")));
        verify(historiqueRepository).save(argThat(h -> h.getCommentaire().contains("double validation requise")));
    }

    @Test
    @DisplayName("Succès : Deuxième validation (montant > 50 000)")
    void approuver_HighAmount_SecondValidation_ShouldApprove() {
        BigDecimal montantFort = new BigDecimal("60000");
        Decision decisionExistante = Decision.builder().id(50L).build();

        when(sinistreRepository.findById(1L)).thenReturn(Optional.of(sinistre));
        when(utilisateurRepository.findById(100L)).thenReturn(Optional.of(superviseur));
        when(decisionRepository.findBySinistreId(1L)).thenReturn(Optional.of(decisionExistante));
        when(decisionRepository.save(any(Decision.class))).thenAnswer(i -> i.getArguments()[0]);
        when(sinistreRepository.save(any(Sinistre.class))).thenAnswer(i -> i.getArguments()[0]);

        Sinistre result = approuverSinistreUseCase.approuver(1L, montantFort, "Validé par second chef", 100L);

        assertThat(result.getStatut()).isEqualTo(StatutSinistre.APPROUVE);
    }

    @Test
    @DisplayName("Échec : Un agent simple ne peut pas approuver")
    void approuver_ShouldFail_WhenNotSuperviseur() {

        Utilisateur agent = Utilisateur.builder().id(2L).role(Role.AGENT).build();
        when(sinistreRepository.findById(1L)).thenReturn(Optional.of(sinistre));
        when(utilisateurRepository.findById(2L)).thenReturn(Optional.of(agent));

        assertThatThrownBy(() -> approuverSinistreUseCase.approuver(1L, BigDecimal.ONE, "test", 2L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Seul un superviseur peut approuver");
    }
}