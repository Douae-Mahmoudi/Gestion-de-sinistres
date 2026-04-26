package com.SinistraPro.domain.exposition.dto.response;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class DecisionResponseTest {

    @Test
    @DisplayName("Succès : Devrait créer une DecisionResponse complète via le Builder")
    void shouldCreateDecisionResponseWithBuilder() {
        Long id = 1L;
        BigDecimal montant = new BigDecimal("1500.50");
        String statut = "APPROUVEE";
        String motif = "Dossier conforme";
        LocalDateTime maintenant = LocalDateTime.now();
        String virement = "VIR-999";
        LocalDate datePaiement = LocalDate.now().plusDays(2);

        UtilisateurResponse superviseur = UtilisateurResponse.builder()
                .nomComplet("Mme. Superviseur")
                .build();

        DecisionResponse response = DecisionResponse.builder()
                .id(id)
                .montantFinal(montant)
                .statut(statut)
                .motif(motif)
                .dateDecision(maintenant)
                .numeroVirement(virement)
                .datePaiement(datePaiement)
                .superviseur(superviseur)
                .build();

        assertThat(response.getId()).isEqualTo(id);
        assertThat(response.getMontantFinal()).isEqualByComparingTo(montant);
        assertThat(response.getStatut()).isEqualTo(statut);
        assertThat(response.getDateDecision()).isEqualTo(maintenant);
        assertThat(response.getSuperviseur().getNomComplet()).isEqualTo("Mme. Superviseur");
    }

    @Test
    @DisplayName("Vérifie les constructeurs NoArgs et AllArgs")
    void shouldVerifyLombokConstructors() {
        DecisionResponse empty = new DecisionResponse();
        assertThat(empty).isNotNull();
        DecisionResponse full = new DecisionResponse(
                1L, BigDecimal.TEN, "REJETEE", "Manque de preuves",
                LocalDateTime.now(), null, null, null
        );
        assertThat(full.getStatut()).isEqualTo("REJETEE");
        assertThat(full.getMontantFinal()).isEqualTo(BigDecimal.TEN);
    }

    @Test
    @DisplayName("Vérifie que les Getters fonctionnent pour les dates")
    void shouldVerifyDateGetters() {
        LocalDate datePaiement = LocalDate.of(2026, 4, 25);
        DecisionResponse response = DecisionResponse.builder()
                .datePaiement(datePaiement)
                .build();

        assertThat(response.getDatePaiement()).isEqualTo(datePaiement);
    }
}