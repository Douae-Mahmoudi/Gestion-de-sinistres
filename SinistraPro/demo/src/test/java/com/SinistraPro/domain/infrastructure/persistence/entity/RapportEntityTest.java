package com.SinistraPro.domain.infrastructure.persistence.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class RapportEntityTest {

    @Test
    @DisplayName("Vérification du Builder et du mapping de RapportEntity")
    void testRapportEntityMapping() {
        LocalDateTime now = LocalDateTime.now();
        UtilisateurEntity expert = UtilisateurEntity.builder().id(5L).nom("Benani").build();
        BigDecimal estimation = new BigDecimal("12500.00");

        // When
        RapportEntity rapport = RapportEntity.builder()
                .id(1L)
                .descriptionDommages("Choc latéral gauche, portière à remplacer")
                .montantEstime(estimation)
                .observations("Véhicule immobilisé au garage")
                .dateSoumission(now)
                .expert(expert)
                .build();

        // Then
        assertThat(rapport.getMontantEstime()).isEqualByComparingTo("12500.00");
        assertThat(rapport.getExpert().getNom()).isEqualTo("Benani");
        assertThat(rapport.getDescriptionDommages()).contains("portière");
        assertThat(rapport.getDateSoumission()).isEqualTo(now);
    }
}