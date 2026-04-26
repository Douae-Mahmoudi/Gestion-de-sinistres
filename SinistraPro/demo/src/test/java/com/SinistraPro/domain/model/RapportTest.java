package com.SinistraPro.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class RapportTest {

    @Test
    @DisplayName("Devrait créer un Rapport complet via le Builder")
    void shouldCreateRapportWithBuilder() {
        LocalDateTime now = LocalDateTime.now();
        BigDecimal estimation = new BigDecimal("2500.75");
        Utilisateur expert = new Utilisateur();

        Rapport rapport = Rapport.builder()
                .id(1L)
                .descriptionDommages("Pare-chocs avant fissuré et phare gauche brisé")
                .montantEstime(estimation)
                .observations("Réparation possible sous 48h")
                .dateSoumission(now)
                .expert(expert)
                .build();

        assertThat(rapport).isNotNull();
        assertThat(rapport.getId()).isEqualTo(1L);
        assertThat(rapport.getDescriptionDommages()).contains("Pare-chocs");
        assertThat(rapport.getMontantEstime()).isEqualByComparingTo(estimation);
        assertThat(rapport.getObservations()).isEqualTo("Réparation possible sous 48h");
        assertThat(rapport.getDateSoumission()).isEqualTo(now);
        assertThat(rapport.getExpert()).isEqualTo(expert);
    }

    @Test
    @DisplayName("Devrait modifier les valeurs via les Setters")
    void shouldUpdateFieldsUsingSetters() {
        Rapport rapport = new Rapport();
        BigDecimal nouveauMontant = new BigDecimal("3000.00");

        rapport.setMontantEstime(nouveauMontant);
        rapport.setDescriptionDommages("Mise à jour suite expertise");

        assertThat(rapport.getMontantEstime()).isEqualByComparingTo(nouveauMontant);
        assertThat(rapport.getDescriptionDommages()).isEqualTo("Mise à jour suite expertise");
    }

    @Test
    @DisplayName("Vérifie la présence des constructeurs requis par Lombok")
    void shouldVerifyLombokAnnotations() {
        Rapport empty = new Rapport();
        assertThat(empty).isNotNull();
        assertThat(empty.getId()).isNull();

        Rapport full = new Rapport(2L, "Dégâts", BigDecimal.TEN, "Obs", null, null);
        assertThat(full.getId()).isEqualTo(2L);
        assertThat(full.getMontantEstime()).isEqualTo(BigDecimal.TEN);
    }
}