package com.SinistraPro.domain.exposition.dto.response;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class SinistreResponseTest {

    @Test
    @DisplayName("Succès : Devrait créer une SinistreResponse complète avec toutes ses relations")
    void shouldCreateCompleteSinistreResponse() {
        UtilisateurResponse client = UtilisateurResponse.builder().nomComplet("M. Client").build();
        UtilisateurResponse agent = UtilisateurResponse.builder().nomComplet("Mme. Agent").build();
        UtilisateurResponse expert = UtilisateurResponse.builder().nomComplet("Cabinet Expert").build();

        RapportResponse rapport = RapportResponse.builder()
                .id(1L)
                .montantEstime(new BigDecimal("5000"))
                .build();

        DecisionResponse decision = DecisionResponse.builder()
                .id(1L)
                .statut("APPROUVEE")
                .build();

        LocalDate dateIncident = LocalDate.of(2026, 4, 15);
        LocalDateTime dateDeclaration = LocalDateTime.now();

        // When
        SinistreResponse response = SinistreResponse.builder()
                .id(100L)
                .numero("SIN-2026-001")
                .typeSinistre("ACCIDENT_AUTO")
                .description("Collision latérale")
                .dateIncident(dateIncident)
                .lieuIncident("Rabat")
                .numeroPolicAssurance("POL-999")
                .numeroConstatAmiable("CST-888")
                .statut("AFFECTE")
                .dateDeclaration(dateDeclaration)
                .client(client)
                .agent(agent)
                .expert(expert)
                .rapport(rapport)
                .decision(decision)
                .build();

        // Then
        assertThat(response.getId()).isEqualTo(100L);
        assertThat(response.getNumero()).isEqualTo("SIN-2026-001");
        assertThat(response.getStatut()).isEqualTo("AFFECTE");
        assertThat(response.getDateIncident()).isEqualTo(dateIncident);

        // Vérification des relations
        assertThat(response.getClient().getNomComplet()).isEqualTo("M. Client");
        assertThat(response.getAgent().getNomComplet()).isEqualTo("Mme. Agent");
        assertThat(response.getExpert().getNomComplet()).isEqualTo("Cabinet Expert");
        assertThat(response.getRapport().getMontantEstime()).isEqualByComparingTo("5000");
        assertThat(response.getDecision().getStatut()).isEqualTo("APPROUVEE");
    }

    @Test
    @DisplayName("Vérifie les constructeurs Lombok (NoArgs/AllArgs)")
    void shouldVerifyLombokConstructors() {
        // NoArgsConstructor pour Jackson
        SinistreResponse empty = new SinistreResponse();
        assertThat(empty).isNotNull();

        // AllArgsConstructor
        SinistreResponse sinistre = SinistreResponse.builder()
                .id(1L)
                .numero("SIN-2026-001")
                .typeSinistre("ACCIDENT")
                .description("Accident sur autoroute")
                .dateIncident(LocalDate.of(2026, 4, 15))
                .lieuIncident("Casablanca")
                .nomAssure("Doe John")
                .localisation("Casablanca")
                .numeroPolicAssurance("POL-2024-001")
                .numeroConstatAmiable("CONST-2024-001")
                .statut("EN_ATTENTE")
                .dateDeclaration(LocalDateTime.now())
                .client(null)
                .agent(null)
                .expert(null)
                .rapport(null)
                .decision(null)
                .build();
    }

    @Test
    @DisplayName("Vérifie qu'un sinistre peut avoir des relations nulles (ex: au début du workflow)")
    void shouldHandleNullRelations() {
        SinistreResponse response = SinistreResponse.builder()
                .id(1L)
                .statut("DECLARE")
                .build();

        assertThat(response.getExpert()).isNull();
        assertThat(response.getRapport()).isNull();
        assertThat(response.getDecision()).isNull();
    }
}