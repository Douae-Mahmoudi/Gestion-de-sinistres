package com.SinistraPro.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SinistreTest {

    @Test
    @DisplayName("Devrait créer un Sinistre complet avec le Builder")
    void shouldCreateSinistreWithBuilder() {
        Utilisateur client = new Utilisateur();
        LocalDate dateIncident = LocalDate.now().minusDays(1);

        Sinistre sinistre = Sinistre.builder()
                .id(1L)
                .numero("SIN-2026-001")
                .typeSinistre("ACCIDENT")
                .statut(StatutSinistre.DECLARE)
                .dateIncident(dateIncident)
                .client(client)
                .build();

        assertThat(sinistre.getNumero()).isEqualTo("SIN-2026-001");
        assertThat(sinistre.getStatut()).isEqualTo(StatutSinistre.DECLARE);
        assertThat(sinistre.getClient()).isEqualTo(client);
    }

    @Nested
    @DisplayName("Tests de la machine à états (validerTransition)")
    class StateMachineTests {

        @Test
        @DisplayName("Devrait passer de DECLARE à AFFECTE sans erreur")
        void shouldTransitionFromDeclareToAffecte() {
            Sinistre sinistre = Sinistre.builder().statut(StatutSinistre.DECLARE).build();

            sinistre.validerTransition(StatutSinistre.AFFECTE);

            assertThat(sinistre.getStatut()).isEqualTo(StatutSinistre.AFFECTE);
        }

        @Test
        @DisplayName("Ne devrait rien faire si le nouveau statut est identique à l'actuel")
        void shouldDoNothingIfStatusIsSame() {
            Sinistre sinistre = Sinistre.builder().statut(StatutSinistre.DECLARE).build();

            sinistre.validerTransition(StatutSinistre.DECLARE);

            assertThat(sinistre.getStatut()).isEqualTo(StatutSinistre.DECLARE);
        }

        @Test
        @DisplayName("Devrait lever une exception pour une transition interdite (DECLARE -> CLOTURE)")
        void shouldThrowExceptionForInvalidTransition() {
            Sinistre sinistre = Sinistre.builder().statut(StatutSinistre.DECLARE).build();

            assertThatThrownBy(() -> sinistre.validerTransition(StatutSinistre.CLOTURE))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Un sinistre déclaré ne peut qu'être affecté");
        }

        @Test
        @DisplayName("Devrait autoriser APPROUVE ou REJETE après EVALUE")
        void shouldAllowMultipleStatesAfterEvalue() {
            Sinistre sinistre = Sinistre.builder().statut(StatutSinistre.EVALUE).build();

            sinistre.validerTransition(StatutSinistre.APPROUVE);
            assertThat(sinistre.getStatut()).isEqualTo(StatutSinistre.APPROUVE);

            sinistre.setStatut(StatutSinistre.EVALUE);
            sinistre.validerTransition(StatutSinistre.REJETE);
            assertThat(sinistre.getStatut()).isEqualTo(StatutSinistre.REJETE);
        }

        @ParameterizedTest
        @EnumSource(value = StatutSinistre.class, names = {"DECLARE", "AFFECTE", "EVALUE"})
        @DisplayName("Ne devrait pas pouvoir modifier un sinistre déjà CLOTURE")
        void shouldNotModifyClotureSinistre(StatutSinistre statutCible) {
            Sinistre sinistre = Sinistre.builder().statut(StatutSinistre.CLOTURE).build();

            assertThatThrownBy(() -> sinistre.validerTransition(statutCible))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Impossible de modifier le statut d'un sinistre terminé");
        }
    }
}