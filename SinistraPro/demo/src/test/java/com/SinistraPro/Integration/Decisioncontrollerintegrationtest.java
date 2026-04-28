package com.SinistraPro.Integration;

import com.SinistraPro.domain.exposition.controller.DecisionController;
import com.SinistraPro.domain.exposition.dto.response.DecisionResponse;
import com.SinistraPro.domain.exposition.exception.GlobalExceptionHandler;
import com.SinistraPro.domain.exposition.mapper.SinistreDtoMapper;
import com.SinistraPro.domain.infrastructure.pdf.PdfGeneratorAdapter;
import com.SinistraPro.domain.infrastructure.security.JwtService;
import com.SinistraPro.domain.model.*;
import com.SinistraPro.domain.port.out.DecisionRepositoryPort;
import com.SinistraPro.domain.port.out.SinistreRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest({DecisionController.class, GlobalExceptionHandler.class})
@DisplayName("Tests d'intégration – DecisionController")
class DecisionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean private JwtService              jwtService;
    @MockBean private DecisionRepositoryPort  decisionRepository;
    @MockBean private SinistreRepositoryPort  sinistreRepository;
    @MockBean private SinistreDtoMapper       dtoMapper;
    @MockBean private PdfGeneratorAdapter     pdfGenerator;

    private Decision        decisionBase;
    private DecisionResponse decisionResponse;
    private Sinistre        sinistreBase;

    @BeforeEach
    void setUp() {
        Utilisateur client = Utilisateur.builder()
                .id(1L).nom("Dupont").prenom("Jean")
                .email("client@test.com").role(Role.CLIENT).build();

        decisionBase = Decision.builder()
                .id(1L)
                .montantFinal(BigDecimal.valueOf(12000))
                .motif("Dossier complet et valide")
                .statut(StatutDecision.APPROUVE)
                .dateDecision(LocalDateTime.now())
                .build();

        decisionResponse = DecisionResponse.builder()
                .id(1L)
                .montantFinal(BigDecimal.valueOf(12000))
                .motif("Dossier complet et valide")
                .statut("APPROUVE")
                .build();

        sinistreBase = Sinistre.builder()
                .id(1L).numero("SP-001")
                .statut(StatutSinistre.APPROUVE)
                .client(client)
                .decision(decisionBase)
                .build();
    }




    @Test
    @WithMockUser(roles = "CLIENT")
    @DisplayName("GET /api/decisions/{sinistreId} – 200 OK quand la décision existe")
    void getDecision_shouldReturn200_whenFound() throws Exception {
        when(decisionRepository.findBySinistreId(1L)).thenReturn(Optional.of(decisionBase));
        when(dtoMapper.toDecisionResponse(decisionBase)).thenReturn(decisionResponse);

        mockMvc.perform(get("/api/decisions/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.montantFinal").value(12000));
    }

    @Test
    @WithMockUser(roles = "CLIENT")
    @DisplayName("GET /api/decisions/{sinistreId} – 404 Not Found quand aucune décision")
    void getDecision_shouldReturn404_whenNotFound() throws Exception {
        when(decisionRepository.findBySinistreId(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/decisions/99"))
                .andExpect(status().isNotFound());
    }



    @Test
    @WithMockUser(roles = "CLIENT")
    @DisplayName("GET /api/decisions/{sinistreId}/pdf – 200 OK avec PDF généré")
    void getDecisionPdf_shouldReturn200_withPdf() throws Exception {
        byte[] fakePdf = "%PDF-1.4 fake content".getBytes();

        when(sinistreRepository.findById(1L)).thenReturn(Optional.of(sinistreBase));
        when(pdfGenerator.genererDecisionPdf(sinistreBase)).thenReturn(fakePdf);

        mockMvc.perform(get("/api/decisions/1/pdf"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"decision-SP-001.pdf\""))
                .andExpect(content().contentType(MediaType.APPLICATION_PDF));
    }


    @Test
    @WithMockUser(roles = "CLIENT")
    @DisplayName("GET /api/decisions/{sinistreId}/pdf – 404 quand le sinistre est introuvable")
    void getDecisionPdf_shouldReturn404_whenSinistreNotFound() throws Exception {
        when(sinistreRepository.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/decisions/99/pdf"))
                .andExpect(status().isNotFound());
    }
}