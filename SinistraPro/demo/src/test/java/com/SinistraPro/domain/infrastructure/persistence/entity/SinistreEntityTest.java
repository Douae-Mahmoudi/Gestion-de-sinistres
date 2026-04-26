package com.SinistraPro.domain.infrastructure.persistence.entity;

import com.SinistraPro.domain.model.StatutSinistre;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class SinistreEntityTest {

    @Test
    @DisplayName("Vérification de l'agrégation complète d'un SinistreEntity")
    void testFullSinistreEntityAggregate() {
        // Given
        UtilisateurEntity client = UtilisateurEntity.builder().id(1L).nom("Alami").build();
        RapportEntity rapport = RapportEntity.builder().id(10L).build();
        DecisionEntity decision = DecisionEntity.builder().id(20L).build();

        // When
        SinistreEntity sinistre = SinistreEntity.builder()
                .numero("SIN-2026-ABC")
                .client(client)
                .statut(StatutSinistre.APPROUVE)
                .rapport(rapport)
                .decision(decision)
                .dateIncident(LocalDate.now())
                .dateDeclaration(LocalDateTime.now())
                .build();

        // Then
        assertThat(sinistre.getNumero()).isEqualTo("SIN-2026-ABC");
        assertThat(sinistre.getClient().getNom()).isEqualTo("Alami");
        assertThat(sinistre.getRapport().getId()).isEqualTo(10L);
        assertThat(sinistre.getDecision().getId()).isEqualTo(20L);
        assertThat(sinistre.getDocuments()).isEmpty(); // Vérifie l'initialisation ArrayList
    }
}