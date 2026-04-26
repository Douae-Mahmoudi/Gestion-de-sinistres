package com.SinistraPro.domain.exposition.controller;

import com.SinistraPro.domain.exposition.dto.response.DecisionResponse;
import com.SinistraPro.domain.exposition.mapper.SinistreDtoMapper;
import com.SinistraPro.domain.infrastructure.pdf.PdfGeneratorAdapter;
import com.SinistraPro.domain.model.Decision;
import com.SinistraPro.domain.model.Sinistre;
import com.SinistraPro.domain.port.out.DecisionRepositoryPort;
import com.SinistraPro.domain.port.out.SinistreRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DecisionControllerTest {

    @Mock private DecisionRepositoryPort decisionRepository;
    @Mock private SinistreRepositoryPort sinistreRepository;
    @Mock private SinistreDtoMapper dtoMapper;
    @Mock private PdfGeneratorAdapter pdfGenerator;

    @InjectMocks
    private DecisionController decisionController;

    private Sinistre sinistre;
    private Decision decision;
    private DecisionResponse decisionResponse;

    @BeforeEach
    void setUp() {
        decision = new Decision();
        decision.setId(1L);
        decision.setMontantFinal(new BigDecimal("45000.00"));

        sinistre = new Sinistre();
        sinistre.setId(10L);
        sinistre.setNumero("SIN-2026-001");
        sinistre.setDecision(decision);

        decisionResponse = DecisionResponse.builder()
                .id(1L)
                .montantFinal(new BigDecimal("45000.00"))
                .statut("APPROUVE")
                .build();
    }

    @Test
    void getDecision_sinistreAvecDecision_retourne200AvecDecision() {
        when(decisionRepository.findBySinistreId(10L)).thenReturn(Optional.of(decision));
        when(dtoMapper.toDecisionResponse(decision)).thenReturn(decisionResponse);

        ResponseEntity<DecisionResponse> response = decisionController.getDecision(10L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatut()).isEqualTo("APPROUVE");
        assertThat(response.getBody().getMontantFinal()).isEqualByComparingTo("45000.00");
    }

    @Test
    void getDecision_sinistreSansDecision_retourne404() {
        when(decisionRepository.findBySinistreId(99L)).thenReturn(Optional.empty());

        ResponseEntity<DecisionResponse> response = decisionController.getDecision(99L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();
    }

    @Test
    void getDecisionPdf_sinistreAvecDecision_retournePdfAvecBonHeader() {
        byte[] pdfBytes = "PDF_CONTENT".getBytes();
        when(sinistreRepository.findById(10L)).thenReturn(Optional.of(sinistre));
        when(pdfGenerator.genererDecisionPdf(sinistre)).thenReturn(pdfBytes);

        ResponseEntity<ByteArrayResource> response = decisionController.getDecisionPdf(10L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_PDF);
        assertThat(response.getHeaders().getFirst("Content-Disposition"))
                .contains("decision-SIN-2026-001.pdf");
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getByteArray()).isEqualTo(pdfBytes);
    }

    @Test
    void getDecisionPdf_sinistreSansDecision_retourne404() {
        Sinistre sinistreSansDecision = new Sinistre();
        sinistreSansDecision.setId(10L);
        sinistreSansDecision.setNumero("SIN-2026-001");
        sinistreSansDecision.setDecision(null);

        when(sinistreRepository.findById(10L)).thenReturn(Optional.of(sinistreSansDecision));

        ResponseEntity<ByteArrayResource> response = decisionController.getDecisionPdf(10L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        verify(pdfGenerator, never()).genererDecisionPdf(any());
    }

    @Test
    void getDecisionPdf_sinistreInexistant_leveIllegalArgumentException() {
        when(sinistreRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> decisionController.getDecisionPdf(99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Sinistre introuvable");
    }

    @Test
    void getDecisionPdf_pdfGenere_contentTypeEstApplicationPdf() {
        byte[] pdfBytes = new byte[]{1, 2, 3};
        when(sinistreRepository.findById(10L)).thenReturn(Optional.of(sinistre));
        when(pdfGenerator.genererDecisionPdf(sinistre)).thenReturn(pdfBytes);

        ResponseEntity<ByteArrayResource> response = decisionController.getDecisionPdf(10L);

        assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_PDF);
    }
}
