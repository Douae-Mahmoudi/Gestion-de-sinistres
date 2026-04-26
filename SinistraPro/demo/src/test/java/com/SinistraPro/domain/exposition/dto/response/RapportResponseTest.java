package com.SinistraPro.domain.exposition.dto.response;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class RapportResponseTest {

    @Test
    @DisplayName("Succès : Devrait créer une RapportResponse complète via le Builder")
    void shouldCreateRapportResponseWithBuilder() {
        Long id = 500L;
        String description = "Choc avant droit, optique et pare-choc à remplacer";
        BigDecimal montant = new BigDecimal("2450.75");
        String obs = "Véhicule roulant mais réparation urgente";
        LocalDateTime maintenant = LocalDateTime.now();

        UtilisateurResponse expert = UtilisateurResponse.builder()
                .nomComplet("Expert Automobile Maroc")
                .email("contact@expert-auto.ma")
                .build();

        RapportResponse response = RapportResponse.builder()
                .id(id)
                .descriptionDommages(description)
                .montantEstime(montant)
                .observations(obs)
                .dateSoumission(maintenant)
                .expert(expert)
                .build();

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(id);
        assertThat(response.getDescriptionDommages()).isEqualTo(description);
        assertThat(response.getMontantEstime()).isEqualByComparingTo(montant);
        assertThat(response.getObservations()).isEqualTo(obs);
        assertThat(response.getDateSoumission()).isEqualTo(maintenant);
        assertThat(response.getExpert().getNomComplet()).isEqualTo("Expert Automobile Maroc");
    }

    @Test
    @DisplayName("Vérifie les constructeurs (NoArgs/AllArgs) pour la sérialisation")
    void shouldVerifyLombokConstructors() {
        RapportResponse empty = new RapportResponse();
        assertThat(empty).isNotNull();

        LocalDateTime date = LocalDateTime.now();
        RapportResponse full = new RapportResponse(
                1L, "Test", BigDecimal.ONE, "Obs", date, null
        );

        assertThat(full.getId()).isEqualTo(1L);
        assertThat(full.getMontantEstime()).isEqualTo(BigDecimal.ONE);
        assertThat(full.getDateSoumission()).isEqualTo(date);
    }

    @Test
    @DisplayName("Vérifie que les Getters Lombok sont opérationnels")
    void shouldVerifyGetters() {
        RapportResponse response = RapportResponse.builder()
                .descriptionDommages("Dégâts des eaux")
                .build();

        assertThat(response.getDescriptionDommages()).isEqualTo("Dégâts des eaux");
    }
}