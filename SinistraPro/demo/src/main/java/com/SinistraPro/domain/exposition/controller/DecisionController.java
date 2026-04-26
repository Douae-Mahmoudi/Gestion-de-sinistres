package com.SinistraPro.domain.exposition.controller;

import com.SinistraPro.domain.model.Sinistre;
import com.SinistraPro.domain.port.out.DecisionRepositoryPort;
import com.SinistraPro.domain.port.out.SinistreRepositoryPort;
import com.SinistraPro.domain.exposition.dto.response.DecisionResponse;
import com.SinistraPro.domain.exposition.mapper.SinistreDtoMapper;
import com.SinistraPro.domain.infrastructure.pdf.PdfGeneratorAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/decisions")
@RequiredArgsConstructor
public class DecisionController {

    private final DecisionRepositoryPort decisionRepository;
    private final SinistreRepositoryPort sinistreRepository;
    private final SinistreDtoMapper dtoMapper;
    private final PdfGeneratorAdapter pdfGenerator;

    @GetMapping("/{sinistreId}")
    public ResponseEntity<DecisionResponse> getDecision(
            @PathVariable Long sinistreId) {

        return decisionRepository.findBySinistreId(sinistreId)
                .map(dtoMapper::toDecisionResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{sinistreId}/pdf")
    public ResponseEntity<ByteArrayResource> getDecisionPdf(
            @PathVariable Long sinistreId) {

        Sinistre sinistre = sinistreRepository.findById(sinistreId)
                .orElseThrow(() -> new IllegalArgumentException("Sinistre introuvable"));

        if (sinistre.getDecision() == null) {
            return ResponseEntity.notFound().build();
        }

        byte[] pdf = pdfGenerator.genererDecisionPdf(sinistre);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"decision-"
                                + sinistre.getNumero() + ".pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(new ByteArrayResource(pdf));
    }
}