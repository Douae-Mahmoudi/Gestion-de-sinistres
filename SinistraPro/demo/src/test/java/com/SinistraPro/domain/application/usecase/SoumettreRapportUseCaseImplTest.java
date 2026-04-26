package com.SinistraPro.domain.application.usecase;

import com.SinistraPro.domain.model.*;
import com.SinistraPro.domain.port.out.HistoriqueRepositoryPort;
import com.SinistraPro.domain.port.out.RapportRepositoryPort;
import com.SinistraPro.domain.port.out.SinistreRepositoryPort;
import com.SinistraPro.domain.port.out.UtilisateurRepositoryPort;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SoumettreRapportUseCaseImplTest {

    @Mock private SinistreRepositoryPort sinistreRepository;
    @Mock private RapportRepositoryPort rapportRepository;
    @Mock private HistoriqueRepositoryPort historiqueRepository;
    @Mock private UtilisateurRepositoryPort utilisateurRepository;

    @Mock private SecurityContext securityContext;
    @Mock private Authentication authentication;

    @InjectMocks
    private SoumettreRapportUseCaseImpl soumettreRapportUseCase;

    private Utilisateur expert;
    private Sinistre sinistre;
    private Rapport rapport;
    private final String emailExpert = "expert@sinistra.ma";

    @BeforeEach
    void setUp() {
        // Mock du contexte de sécurité Spring
        SecurityContextHolder.setContext(securityContext);

        expert = Utilisateur.builder()
                .id(10L)
                .email(emailExpert)
                .role(Role.EXPERT)
                .build();

        sinistre = Sinistre.builder()
                .id(1L)
                .statut(StatutSinistre.AFFECTE)
                .expert(expert)
                .build();

        rapport = Rapport.builder()
                .descriptionDommages("Choc avant")
                .montantEstime(new BigDecimal("5000"))
                .build();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Succès : Soumission du rapport par l'expert affecté")
    void soumettre_ShouldSucceed_WhenExpertIsAssigned() {
        // Given
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(emailExpert);
        when(utilisateurRepository.findByEmail(emailExpert)).thenReturn(Optional.of(expert));
        when(sinistreRepository.findById(1L)).thenReturn(Optional.of(sinistre));
        when(rapportRepository.save(any(Rapport.class))).thenAnswer(i -> i.getArguments()[0]);
        when(sinistreRepository.save(any(Sinistre.class))).thenAnswer(i -> i.getArguments()[0]);

        // When
        Sinistre result = soumettreRapportUseCase.soumettre(1L, rapport);

        // Then
        assertThat(result.getStatut()).isEqualTo(StatutSinistre.EVALUE);
        assertThat(result.getRapport()).isNotNull();
        verify(historiqueRepository).save(any(Historique.class));
        verify(sinistreRepository).save(any(Sinistre.class));
    }

    @Test
    @DisplayName("Échec : Un autre expert tente de soumettre le rapport")
    void soumettre_ShouldFail_WhenDifferentExpertTriesToSubmit() {
        // Given
        String emailAutreExpert = "autre@expert.ma";
        Utilisateur autreExpert = Utilisateur.builder().id(99L).email(emailAutreExpert).build();

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(emailAutreExpert);
        when(utilisateurRepository.findByEmail(emailAutreExpert)).thenReturn(Optional.of(autreExpert));
        when(sinistreRepository.findById(1L)).thenReturn(Optional.of(sinistre));

        // When & Then
        assertThatThrownBy(() -> soumettreRapportUseCase.soumettre(1L, rapport))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Seul l'expert affecté peut soumettre");
    }

    @Test
    @DisplayName("Échec : Aucun expert n'est affecté au sinistre")
    void soumettre_ShouldFail_WhenNoExpertAssigned() {
        // Given
        sinistre.setExpert(null);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(emailExpert);
        when(utilisateurRepository.findByEmail(emailExpert)).thenReturn(Optional.of(expert));
        when(sinistreRepository.findById(1L)).thenReturn(Optional.of(sinistre));

        assertThatThrownBy(() -> soumettreRapportUseCase.soumettre(1L, rapport))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Aucun expert affecté à ce sinistre");
    }
}