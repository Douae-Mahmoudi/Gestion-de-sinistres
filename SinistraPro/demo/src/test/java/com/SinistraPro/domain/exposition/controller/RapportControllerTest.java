package com.SinistraPro.domain.exposition.controller;

import com.SinistraPro.domain.exposition.dto.response.RapportResponse;
import com.SinistraPro.domain.exposition.mapper.SinistreDtoMapper;
import com.SinistraPro.domain.infrastructure.pdf.PdfGeneratorAdapter;
import com.SinistraPro.domain.model.Rapport;
import com.SinistraPro.domain.model.Sinistre;
import com.SinistraPro.domain.port.out.RapportRepositoryPort;
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
class RapportControllerTest {

    @Mock private RapportRepositoryPort rapportRepository;
    @Mock private SinistreRepositoryPort sinistreRepository;
    @Mock private SinistreDtoMapper dtoMapper;
    @Mock private PdfGeneratorAdapter pdfGenerator;

    @InjectMocks
    private RapportController rapportController;

    private Sinistre sinistre;
    private Rapport rapport;
    private RapportResponse rapportResponse;

    @BeforeEach
    void setUp() {
        rapport = new Rapport();
        rapport.setId(1L);
        rapport.setDescriptionDommages("Carrosserie endommagée");
        rapport.setMontantEstime(new BigDecimal("15000.00"));

        sinistre = new Sinistre();
        sinistre.setId(10L);
        sinistre.setNumero("SIN-2026-001");
        sinistre.setRapport(rapport);

        rapportResponse = RapportResponse.builder()
                .id(1L)
                .descriptionDommages("Carrosserie endommagée")
                .montantEstime(new BigDecimal("15000.00"))
                .build();
    }

    @Test
    void getBySinistre_sinistreAvecRapport_retourne200() {
        when(rapportRepository.findBySinistreId(10L)).thenReturn(Optional.of(rapport));
        when(dtoMapper.toRapportResponse(rapport)).thenReturn(rapportResponse);

        ResponseEntity<RapportResponse> response = rapportController.getBysinistre(10L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getDescriptionDommages()).isEqualTo("Carrosserie endommagée");
        assertThat(response.getBody().getMontantEstime()).isEqualByComparingTo("15000.00");
    }

    @Test
    void getBySinistre_sinistreSansRapport_retourne404() {
        when(rapportRepository.findBySinistreId(99L)).thenReturn(Optional.empty());

        ResponseEntity<RapportResponse> response = rapportController.getBysinistre(99L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();
    }

    @Test
    void getRapportPdf_sinistreAvecRapport_retournePdf() {
        byte[] pdfBytes = "PDF_RAPPORT".getBytes();
        when(sinistreRepository.findById(10L)).thenReturn(Optional.of(sinistre));
        when(pdfGenerator.genererRapportPdf(sinistre, rapport)).thenReturn(pdfBytes);

        ResponseEntity<ByteArrayResource> response = rapportController.getRapportPdf(10L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_PDF);
        assertThat(response.getHeaders().getFirst("Content-Disposition"))
                .contains("rapport-10.pdf");
        assertThat(response.getBody().getByteArray()).isEqualTo(pdfBytes);
    }

    @Test
    void getRapportPdf_sinistreSansRapport_retourne404() {
        Sinistre sinistreSansRapport = new Sinistre();
        sinistreSansRapport.setId(10L);
        sinistreSansRapport.setRapport(null);

        when(sinistreRepository.findById(10L)).thenReturn(Optional.of(sinistreSansRapport));

        ResponseEntity<ByteArrayResource> response = rapportController.getRapportPdf(10L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        verify(pdfGenerator, never()).genererRapportPdf(any(), any());
    }

    @Test
    void getRapportPdf_sinistreInexistant_leveException() {
        when(sinistreRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> rapportController.getRapportPdf(99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Sinistre introuvable");
    }
}