package com.SinistraPro.domain.exposition.dto.response;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class UtilisateurResponseTest {


    @Test
    void utilisateurResponse_builder_creationAvecTousLesChamps() {
        LocalDateTime dateCreation = LocalDateTime.of(2026, 1, 15, 9, 30);

        UtilisateurResponse response = UtilisateurResponse.builder()
                .id(1L)
                .nom("Doe")
                .prenom("John")
                .nomComplet("John Doe")
                .email("john.doe@test.com")
                .telephone("0612345678")
                .role("CLIENT")
                .dateCreation(dateCreation)
                .adresse("12 Rue Mohammed V, Casablanca")
                .build();

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getNom()).isEqualTo("Doe");
        assertThat(response.getPrenom()).isEqualTo("John");
        assertThat(response.getNomComplet()).isEqualTo("John Doe");
        assertThat(response.getEmail()).isEqualTo("john.doe@test.com");
        assertThat(response.getTelephone()).isEqualTo("0612345678");
        assertThat(response.getRole()).isEqualTo("CLIENT");
        assertThat(response.getDateCreation()).isEqualTo(dateCreation);
        assertThat(response.getAdresse()).isEqualTo("12 Rue Mohammed V, Casablanca");
    }

    @Test
    void utilisateurResponse_constructeurVide_tousLesChampsSontNuls() {

        UtilisateurResponse response = new UtilisateurResponse();

        assertThat(response.getId()).isNull();
        assertThat(response.getNom()).isNull();
        assertThat(response.getPrenom()).isNull();
        assertThat(response.getNomComplet()).isNull();
        assertThat(response.getEmail()).isNull();
        assertThat(response.getTelephone()).isNull();
        assertThat(response.getRole()).isNull();
        assertThat(response.getDateCreation()).isNull();
        assertThat(response.getAdresse()).isNull();
    }

    @Test
    void utilisateurResponse_constructeurComplet_champsCorrectementInitialises() {
        LocalDateTime dateCreation = LocalDateTime.of(2026, 3, 10, 8, 0);

        UtilisateurResponse response = new UtilisateurResponse(
                2L,
                "Martin",
                "Sophie",
                "Sophie Martin",
                "sophie@test.com",
                "0698765432",
                "AGENT",
                dateCreation,
                "Rabat, Maroc"
        );

        // THEN
        assertThat(response.getId()).isEqualTo(2L);
        assertThat(response.getNom()).isEqualTo("Martin");
        assertThat(response.getPrenom()).isEqualTo("Sophie");
        assertThat(response.getNomComplet()).isEqualTo("Sophie Martin");
        assertThat(response.getEmail()).isEqualTo("sophie@test.com");
        assertThat(response.getRole()).isEqualTo("AGENT");
        assertThat(response.getDateCreation()).isEqualTo(dateCreation);
    }



    @Test
    void utilisateurResponse_rolesMultiples_sontCorrectementStockes() {
        // WHEN & THEN
        for (String role : new String[]{"CLIENT", "AGENT", "EXPERT", "SUPERVISEUR"}) {
            UtilisateurResponse response = UtilisateurResponse.builder()
                    .role(role)
                    .build();
            assertThat(response.getRole()).isEqualTo(role);
        }
    }



    @Test
    void utilisateurResponse_sansAdresse_adresseEstNulle() {
        UtilisateurResponse response = UtilisateurResponse.builder()
                .id(1L)
                .nom("Doe")
                .prenom("John")
                .email("john@test.com")
                .role("CLIENT")
                .build();

        assertThat(response.getAdresse()).isNull();
        assertThat(response.getNomComplet()).isNull();
    }

    @Test
    void utilisateurResponse_emailValide_estCorrectementStocke() {
        // WHEN
        UtilisateurResponse response = UtilisateurResponse.builder()
                .email("expert@sinistrapro.ma")
                .role("EXPERT")
                .build();

        // THEN
        assertThat(response.getEmail()).contains("@");
        assertThat(response.getEmail()).isEqualTo("expert@sinistrapro.ma");
    }

    @Test
    void utilisateurResponse_dateCreation_estCorrectementStockee() {
        // GIVEN
        LocalDateTime maintenant = LocalDateTime.now();

        // WHEN
        UtilisateurResponse response = UtilisateurResponse.builder()
                .dateCreation(maintenant)
                .build();

        // THEN
        assertThat(response.getDateCreation()).isEqualTo(maintenant);
        assertThat(response.getDateCreation()).isBefore(LocalDateTime.now().plusSeconds(1));
    }
}
