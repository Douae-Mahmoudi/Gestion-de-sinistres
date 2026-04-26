package com.SinistraPro.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class HistoriqueTest {

    @Test
    @DisplayName("Devrait créer un enregistrement d'historique via le Builder")
    void shouldCreateHistoriqueWithBuilder() {
        LocalDateTime now = LocalDateTime.now();
        Utilisateur agent = new Utilisateur();
        Long sinistreId = 100L;

        Historique historique = Historique.builder()
                .id(1L)
                .sinistreId(sinistreId)
                .ancienStatut(StatutSinistre.DECLARE)
                .nouveauStatut(StatutSinistre.AFFECTE)
                .commentaire("Affectation automatique à l'expert")
                .effectuePar(agent)
                .dateAction(now)
                .build();

        assertThat(historique).isNotNull();
        assertThat(historique.getId()).isEqualTo(1L);
        assertThat(historique.getSinistreId()).isEqualTo(sinistreId);
        assertThat(historique.getAncienStatut()).isEqualTo(StatutSinistre.DECLARE);
        assertThat(historique.getNouveauStatut()).isEqualTo(StatutSinistre.AFFECTE);
        assertThat(historique.getCommentaire()).isEqualTo("Affectation automatique à l'expert");
        assertThat(historique.getEffectuePar()).isEqualTo(agent);
        assertThat(historique.getDateAction()).isEqualTo(now);
    }

    @Test
    @DisplayName("Devrait modifier les données via les Setters")
    void shouldUpdateHistoriqueUsingSetters() {
        Historique historique = new Historique();
        String nouveauCommentaire = "Correction manuelle";

        historique.setCommentaire(nouveauCommentaire);
        historique.setSinistreId(200L);

        assertThat(historique.getCommentaire()).isEqualTo(nouveauCommentaire);
        assertThat(historique.getSinistreId()).isEqualTo(200L);
    }

    @Test
    @DisplayName("Vérifie que les constructeurs Lombok sont fonctionnels")
    void shouldVerifyLombokConstructors() {
        Historique empty = new Historique();
        assertThat(empty).isNotNull();

        Historique full = new Historique(1L, 10L, StatutSinistre.EVALUE, StatutSinistre.APPROUVE, "OK", null, null);
        assertThat(full.getAncienStatut()).isEqualTo(StatutSinistre.EVALUE);
    }
}