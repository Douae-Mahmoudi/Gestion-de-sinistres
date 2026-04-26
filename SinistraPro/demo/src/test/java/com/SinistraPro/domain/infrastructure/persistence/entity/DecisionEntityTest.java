package com.SinistraPro.domain.infrastructure.persistence.entity;

import com.SinistraPro.domain.model.StatutDecision;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class DecisionEntityTest {

    @Test
    @DisplayName("Vérification du fonctionnement du Builder et des Getters")
    void testDecisionEntityBuilderAndGetters() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        UtilisateurEntity superviseur = new UtilisateurEntity();
        superviseur.setId(1L);

        // When
        DecisionEntity entity = DecisionEntity.builder()
                .id(100L)
                .montantFinal(new BigDecimal("1500.50"))
                .statut(StatutDecision.APPROUVE)
                .motif("Dossier conforme")
                .dateDecision(now)
                .superviseur(superviseur)
                .build();

        // Then
        assertThat(entity.getId()).isEqualTo(100L);
        assertThat(entity.getMontantFinal()).isEqualByComparingTo("1500.50");
        assertThat(entity.getStatut()).isEqualTo(StatutDecision.APPROUVE);
        assertThat(entity.getSuperviseur().getId()).isEqualTo(1L);
        assertThat(entity.getDateDecision()).isEqualTo(now);
    }

    @Test
    @DisplayName("Vérification de la mutabilité (Setters)")
    void testSetters() {
        // Given
        DecisionEntity entity = new DecisionEntity();

        // When
        entity.setNumeroVirement("VIR-TEST-123");

        // Then
        assertThat(entity.getNumeroVirement()).isEqualTo("VIR-TEST-123");
    }
}