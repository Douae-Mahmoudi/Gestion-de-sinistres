package com.SinistraPro.Integration;

import com.SinistraPro.domain.exposition.controller.RapportController;
import com.SinistraPro.domain.exposition.dto.response.RapportResponse;
import com.SinistraPro.domain.exposition.exception.GlobalExceptionHandler;
import com.SinistraPro.domain.exposition.mapper.SinistreDtoMapper;
import com.SinistraPro.domain.infrastructure.pdf.PdfGeneratorAdapter;
import com.SinistraPro.domain.infrastructure.security.JwtService;
import com.SinistraPro.domain.model.*;
import com.SinistraPro.domain.port.out.RapportRepositoryPort;
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
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest({RapportController.class, GlobalExceptionHandler.class})
@DisplayName("Tests d'intégration – RapportController")
class RapportControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean private JwtService             jwtService;
    @MockBean private RapportRepositoryPort  rapportRepository;
    @MockBean private SinistreRepositoryPort sinistreRepository;
    @MockBean private SinistreDtoMapper      dtoMapper;
    @MockBean private PdfGeneratorAdapter    pdfGenerator;

    private Rapport        rapportBase;
    private RapportResponse rapportResponse;
    private Sinistre       sinistreBase;

    @BeforeEach
    void setUp() {
        Utilisateur expert = Utilisateur.builder()
                .id(3L).nom("Expert").prenom("Karim")
                .email("expert@test.com").role(Role.EXPERT).build();

        Utilisateur client = Utilisateur.builder()
                .id(1L).nom("Dupont").prenom("Jean")
                .email("client@test.com").role(Role.CLIENT).build();

        rapportBase = Rapport.builder()
                .id(1L)
                .descriptionDommages("Dommages importants sur la carrosserie")
                .montantEstime(BigDecimal.valueOf(15000))
                .observations("Aucune fraude détectée")
                .expert(expert)
                .build();

        rapportResponse = RapportResponse.builder()
                .id(1L)
                .descriptionDommages("Dommages importants sur la carrosserie")
                .montantEstime(BigDecimal.valueOf(15000))
                .observations("Aucune fraude détectée")
                .build();

        sinistreBase = Sinistre.builder()
                .id(1L).numero("SP-001")
                .statut(StatutSinistre.EVALUE)
                .client(client).expert(expert)
                .rapport(rapportBase)
                .build();
    }


    @Test
    @WithMockUser(roles = "SUPERVISEUR")
    @DisplayName("GET /api/rapports/{sinistreId} – 200 OK quand le rapport existe")
    void getRapport_shouldReturn200_whenFound() throws Exception {
        when(rapportRepository.findBySinistreId(1L)).thenReturn(Optional.of(rapportBase));
        when(dtoMapper.toRapportResponse(rapportBase)).thenReturn(rapportResponse);

        mockMvc.perform(get("/api/rapports/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.montantEstime").value(15000));
    }

    @Test
    @WithMockUser(roles = "SUPERVISEUR")
    @DisplayName("GET /api/rapports/{sinistreId} – 404 Not Found quand le rapport est absent")
    void getRapport_shouldReturn404_whenNotFound() throws Exception {
        when(rapportRepository.findBySinistreId(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/rapports/99"))
                .andExpect(status().isNotFound());
    }


    @Test
    @WithMockUser(roles = "SUPERVISEUR")
    @DisplayName("GET /api/rapports/{sinistreId}/pdf – 200 OK avec PDF généré")
    void getRapportPdf_shouldReturn200_withPdf() throws Exception {
        byte[] fakePdf = "%PDF-1.4 rapport content".getBytes();

        when(sinistreRepository.findById(1L)).thenReturn(Optional.of(sinistreBase));
        when(pdfGenerator.genererRapportPdf(sinistreBase, rapportBase)).thenReturn(fakePdf);

        mockMvc.perform(get("/api/rapports/1/pdf"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"rapport-1.pdf\""))
                .andExpect(content().contentType(MediaType.APPLICATION_PDF));
    }


    @Test
    @WithMockUser(roles = "SUPERVISEUR")
    @DisplayName("GET /api/rapports/{sinistreId}/pdf – 404 quand le sinistre est introuvable")
    void getRapportPdf_shouldReturn404_whenSinistreNotFound() throws Exception {
        when(sinistreRepository.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/rapports/99/pdf"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Sinistre introuvable"));
    }
}