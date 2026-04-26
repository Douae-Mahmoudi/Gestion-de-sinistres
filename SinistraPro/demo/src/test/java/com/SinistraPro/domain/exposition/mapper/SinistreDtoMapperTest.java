package com.SinistraPro.domain.exposition.mapper;

import com.SinistraPro.domain.exposition.dto.response.*;
import com.SinistraPro.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class SinistreDtoMapperTest {

    private SinistreDtoMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new SinistreDtoMapper();
    }

    @Test
    @DisplayName("Devrait mapper un Sinistre complet vers SinistreResponse")
    void shouldMapSinistreToResponse() {
        Utilisateur client = createUtilisateur(1L, "Alami", Role.CLIENT);
        Utilisateur agent = createUtilisateur(2L, "Dupont", Role.AGENT);

        Sinistre sinistre = Sinistre.builder()
                .id(100L)
                .numero("SIN-001")
                .statut(StatutSinistre.AFFECTE)
                .client(client)
                .agent(agent)
                .dateIncident(LocalDate.now())
                .build();

        SinistreResponse response = mapper.toResponse(sinistre);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(100L);
        assertThat(response.getStatut()).isEqualTo("AFFECTE");
        assertThat(response.getClient().getNom()).isEqualTo("Alami");
        assertThat(response.getAgent().getRole()).isEqualTo("AGENT");
    }

    @Test
    @DisplayName("Devrait mapper un Rapport vers RapportResponse avec son expert")
    void shouldMapRapportToResponse() {
        // Given
        Utilisateur expert = createUtilisateur(3L, "Expertise-Pro", Role.EXPERT);
        Rapport rapport = Rapport.builder()
                .id(10L)
                .montantEstime(new BigDecimal("1500"))
                .expert(expert)
                .build();

        // When
        RapportResponse response = mapper.toRapportResponse(rapport);

        // Then
        assertThat(response.getMontantEstime()).isEqualByComparingTo("1500");
        assertThat(response.getExpert().getNom()).isEqualTo("Expertise-Pro");
    }

    @Test
    @DisplayName("Devrait gérer les objets nuls en retournant null")
    void shouldReturnNullWhenSourceIsNull() {
        assertThat(mapper.toResponse(null)).isNull();
        assertThat(mapper.toUtilisateurResponse(null)).isNull();
        assertThat(mapper.toRapportResponse(null)).isNull();
    }

    @Test
    @DisplayName("Devrait mapper l'historique même sans ancien statut")
    void shouldMapHistoriqueWithNullAncienStatut() {
        // Given
        Historique h = Historique.builder()
                .nouveauStatut(StatutSinistre.DECLARE)
                .ancienStatut(null)
                .build();

        // When
        HistoriqueResponse response = mapper.toHistoriqueResponse(h);

        // Then
        assertThat(response.getAncienStatut()).isNull();
        assertThat(response.getNouveauStatut()).isEqualTo("DECLARE");
    }


    private Utilisateur createUtilisateur(Long id, String nom, Role role) {
        return Utilisateur.builder()
                .id(id)
                .nom(nom)
                .role(role)
                .build();
    }
}