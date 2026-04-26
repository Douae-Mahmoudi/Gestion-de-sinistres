package com.SinistraPro.domain.infrastructure.persistence.entity;

import com.SinistraPro.domain.model.StatutSinistre;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class HistoriqueEntityTest {

    @Test
    @DisplayName("Vérification du mapping et de la cohérence de HistoriqueEntity")
    void testHistoriqueEntityMapping() {
        LocalDateTime now = LocalDateTime.now();
        UtilisateurEntity agent = UtilisateurEntity.builder().id(2L).nom("Alami").build();
        SinistreEntity sinistre = SinistreEntity.builder().id(50L).build();

        HistoriqueEntity historique = HistoriqueEntity.builder()
                .id(1L)
                .ancienStatut(StatutSinistre.DECLARE)
                .nouveauStatut(StatutSinistre.AFFECTE)
                .commentaire("Affectation de l'expert Benani")
                .effectuePar(agent)
                .sinistre(sinistre)
                .dateAction(now)
                .build();

        // Then
        assertThat(historique.getAncienStatut()).isEqualTo(StatutSinistre.DECLARE);
        assertThat(historique.getNouveauStatut()).isEqualTo(StatutSinistre.AFFECTE);
        assertThat(historique.getEffectuePar().getNom()).isEqualTo("Alami");
        assertThat(historique.getSinistre().getId()).isEqualTo(50L);
        assertThat(historique.getDateAction()).isEqualTo(now);
    }
}