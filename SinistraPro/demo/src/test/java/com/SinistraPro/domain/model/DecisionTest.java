package com.SinistraPro.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class DecisionTest {

    @Test
    @DisplayName("Devrait créer une instance de Decision via le Builder")
    void shouldCreateDecisionWithBuilder() {
        LocalDateTime now = LocalDateTime.now();
        LocalDate today = LocalDate.now();
        BigDecimal montant = new BigDecimal("1500.50");
        Utilisateur mockUser = new Utilisateur();

        Decision decision = Decision.builder()
                .id(1L)
                .montantFinal(montant)
                .statut(StatutDecision.APPROUVE)
                .motif("Dossier complet")
                .dateDecision(now)
                .numeroVirement("VIR-2024-001")
                .datePaiement(today)
                .superviseur(mockUser)
                .build();

        assertThat(decision).isNotNull();
        assertThat(decision.getId()).isEqualTo(1L);
        assertThat(decision.getMontantFinal()).isEqualByComparingTo(montant);
        assertThat(decision.getStatut()).isEqualTo(StatutDecision.APPROUVE);
        assertThat(decision.getMotif()).isEqualTo("Dossier complet");
        assertThat(decision.getDateDecision()).isEqualTo(now);
        assertThat(decision.getNumeroVirement()).isEqualTo("VIR-2024-001");
        assertThat(decision.getDatePaiement()).isEqualTo(today);
        assertThat(decision.getSuperviseur()).isEqualTo(mockUser);
    }

    @Test
    @DisplayName("Devrait modifier les champs via les Setters")
    void shouldUpdateFieldsUsingSetters() {
        Decision decision = new Decision();
        String nouveauMotif = "Refus technique";

        decision.setMotif(nouveauMotif);
        decision.setId(10L);

        assertThat(decision.getMotif()).isEqualTo(nouveauMotif);
        assertThat(decision.getId()).isEqualTo(10L);
    }

    @Test
    @DisplayName("Devrait tester l'égalité et le NoArgsConstructor")
    void shouldTestNoArgsConstructor() {
        Decision decision = new Decision();

        assertThat(decision).isNotNull();
        assertThat(decision.getId()).isNull();
    }
}