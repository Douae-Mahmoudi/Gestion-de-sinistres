package com.SinistraPro.domain.exposition.dto.response;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class HistoriqueResponseTest {

    @Test
    @DisplayName("Succès : Devrait créer une HistoriqueResponse via le Builder")
    void shouldCreateHistoriqueResponseWithBuilder() {
        Long id = 10L;
        String ancienStatut = "DECLARE";
        String nouveauStatut = "AFFECTE";
        String commentaire = "Expert affecté pour évaluation";
        LocalDateTime dateAction = LocalDateTime.now();

        UtilisateurResponse effectuePar = UtilisateurResponse.builder()
                .nomComplet("M. L'Agent")
                .role("AGENT")
                .build();

        HistoriqueResponse response = HistoriqueResponse.builder()
                .id(id)
                .ancienStatut(ancienStatut)
                .nouveauStatut(nouveauStatut)
                .commentaire(commentaire)
                .effectuePar(effectuePar)
                .dateAction(dateAction)
                .build();

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(id);
        assertThat(response.getAncienStatut()).isEqualTo(ancienStatut);
        assertThat(response.getNouveauStatut()).isEqualTo(nouveauStatut);
        assertThat(response.getCommentaire()).isEqualTo(commentaire);
        assertThat(response.getDateAction()).isEqualTo(dateAction);
        assertThat(response.getEffectuePar().getNomComplet()).isEqualTo("M. L'Agent");
    }

    @Test
    @DisplayName("Vérifie les constructeurs NoArgs et AllArgs de Lombok")
    void shouldVerifyLombokConstructors() {
        HistoriqueResponse empty = new HistoriqueResponse();
        assertThat(empty).isNotNull();

        LocalDateTime now = LocalDateTime.now();
        HistoriqueResponse full = new HistoriqueResponse(
                1L, "VALIDE", "CLOTURE", "Paiement effectué", null, now
        );

        assertThat(full.getNouveauStatut()).isEqualTo("CLOTURE");
        assertThat(full.getDateAction()).isEqualTo(now);
    }

    @Test
    @DisplayName("Vérifie l'intégrité des Getters")
    void shouldVerifyGetters() {
        // Given
        HistoriqueResponse response = HistoriqueResponse.builder()
                .commentaire("Test de commentaire")
                .build();

        assertThat(response.getCommentaire()).isEqualTo("Test de commentaire");
    }
}