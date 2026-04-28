package com.SinistraPro.Integration;

import com.SinistraPro.domain.exposition.controller.StatsController;
import com.SinistraPro.domain.infrastructure.security.JwtService;
import com.SinistraPro.domain.model.*;
import com.SinistraPro.domain.port.out.SinistreRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StatsController.class)
@DisplayName("Tests d'intégration – StatsController")
class StatsControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean private JwtService             jwtService;
    @MockBean private SinistreRepositoryPort sinistreRepository;

    private Sinistre sinistreDeclare;
    private Sinistre sinistreApprouve;
    private Sinistre sinistreRejete;
    private Sinistre sinistreCloture;

    @BeforeEach
    void setUp() {
        Utilisateur client = Utilisateur.builder()
                .id(1L).email("client@test.com").role(Role.CLIENT).build();

        sinistreDeclare = Sinistre.builder()
                .id(1L).numero("SP-001")
                .statut(StatutSinistre.DECLARE)
                .dateIncident(LocalDate.of(2024, 1, 10))
                .client(client).build();

        sinistreApprouve = Sinistre.builder()
                .id(2L).numero("SP-002")
                .statut(StatutSinistre.APPROUVE)
                .dateIncident(LocalDate.of(2024, 2, 15))
                .client(client).build();

        sinistreRejete = Sinistre.builder()
                .id(3L).numero("SP-003")
                .statut(StatutSinistre.REJETE)
                .dateIncident(LocalDate.of(2024, 3, 20))
                .client(client).build();

        sinistreCloture = Sinistre.builder()
                .id(4L).numero("SP-004")
                .statut(StatutSinistre.CLOTURE)
                .dateIncident(LocalDate.of(2024, 4, 5))
                .client(client).build();
    }



    @Test
    @WithMockUser(roles = "SUPERVISEUR")
    @DisplayName("GET /api/stats/sinistres-par-statut – 200 OK avec compteurs par statut")
    void parStatut_shouldReturn200_withCountsPerStatus() throws Exception {

        when(sinistreRepository.findByStatut(StatutSinistre.DECLARE))
                .thenReturn(List.of(sinistreDeclare));
        when(sinistreRepository.findByStatut(StatutSinistre.APPROUVE))
                .thenReturn(List.of(sinistreApprouve));
        when(sinistreRepository.findByStatut(StatutSinistre.REJETE))
                .thenReturn(List.of(sinistreRejete));
        when(sinistreRepository.findByStatut(StatutSinistre.CLOTURE))
                .thenReturn(List.of(sinistreCloture));
        for (StatutSinistre s : StatutSinistre.values()) {
            if (s != StatutSinistre.DECLARE && s != StatutSinistre.APPROUVE
                    && s != StatutSinistre.REJETE && s != StatutSinistre.CLOTURE) {
                when(sinistreRepository.findByStatut(s)).thenReturn(List.of());
            }
        }

        mockMvc.perform(get("/api/stats/sinistres-par-statut"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.DECLARE").value(1))
                .andExpect(jsonPath("$.APPROUVE").value(1))
                .andExpect(jsonPath("$.REJETE").value(1))
                .andExpect(jsonPath("$.CLOTURE").value(1));
    }

    @Test
    @DisplayName("GET /api/stats/sinistres-par-statut – 401/403 quand non authentifié")
    void parStatut_shouldReturn4xx_whenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/stats/sinistres-par-statut"))
                .andExpect(status().is4xxClientError());
    }



    @Test
    @WithMockUser(roles = "SUPERVISEUR")
    @DisplayName("GET /api/stats/resume – 200 OK avec le résumé global correct")
    void resume_shouldReturn200_withCorrectSummary() throws Exception {

        when(sinistreRepository.findAll())
                .thenReturn(List.of(sinistreDeclare, sinistreApprouve,
                        sinistreRejete, sinistreCloture));
        when(sinistreRepository.findByStatut(StatutSinistre.APPROUVE))
                .thenReturn(List.of(sinistreApprouve));
        when(sinistreRepository.findByStatut(StatutSinistre.REJETE))
                .thenReturn(List.of(sinistreRejete));
        when(sinistreRepository.findByStatut(StatutSinistre.CLOTURE))
                .thenReturn(List.of(sinistreCloture));

        mockMvc.perform(get("/api/stats/resume"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(4))
                .andExpect(jsonPath("$.enCours").value(1))
                .andExpect(jsonPath("$.approuves").value(1))
                .andExpect(jsonPath("$.rejetes").value(1))
                .andExpect(jsonPath("$.clotures").value(1));
    }

    @Test
    @WithMockUser(roles = "AGENT")
    @DisplayName("GET /api/stats/resume – 200 OK avec zéro sinistre en base")
    void resume_shouldReturn200_withAllZerosWhenEmpty() throws Exception {

        when(sinistreRepository.findAll()).thenReturn(List.of());
        when(sinistreRepository.findByStatut(StatutSinistre.APPROUVE)).thenReturn(List.of());
        when(sinistreRepository.findByStatut(StatutSinistre.REJETE)).thenReturn(List.of());
        when(sinistreRepository.findByStatut(StatutSinistre.CLOTURE)).thenReturn(List.of());

        mockMvc.perform(get("/api/stats/resume"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(0))
                .andExpect(jsonPath("$.enCours").value(0))
                .andExpect(jsonPath("$.approuves").value(0))
                .andExpect(jsonPath("$.rejetes").value(0))
                .andExpect(jsonPath("$.clotures").value(0));
    }
}