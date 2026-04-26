package com.SinistraPro.domain.exposition.controller;

import com.SinistraPro.domain.model.Sinistre;
import com.SinistraPro.domain.port.out.RapportRepositoryPort;
import com.SinistraPro.domain.port.out.SinistreRepositoryPort;
import com.SinistraPro.domain.exposition.dto.response.RapportResponse;
import com.SinistraPro.domain.exposition.mapper.SinistreDtoMapper;
import com.SinistraPro.domain.infrastructure.pdf.PdfGeneratorAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rapports")
@RequiredArgsConstructor
public class RapportController {

    private final RapportRepositoryPort rapportRepository;
    private final SinistreRepositoryPort sinistreRepository;
    private final SinistreDtoMapper dtoMapper;
    private final PdfGeneratorAdapter pdfGenerator;

    @GetMapping("/{sinistreId}")
    public ResponseEntity<RapportResponse> getBysinistre(
            @PathVariable Long sinistreId) {

        return rapportRepository.findBySinistreId(sinistreId)
                .map(dtoMapper::toRapportResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{sinistreId}/pdf")
    public ResponseEntity<ByteArrayResource> getRapportPdf(
            @PathVariable Long sinistreId) {

        Sinistre sinistre = sinistreRepository.findById(sinistreId)
                .orElseThrow(() -> new IllegalArgumentException("Sinistre introuvable"));

        if (sinistre.getRapport() == null) {
            return ResponseEntity.notFound().build();
        }

        byte[] pdf = pdfGenerator.genererRapportPdf(sinistre, sinistre.getRapport());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"rapport-" + sinistreId + ".pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(new ByteArrayResource(pdf));
    }
}