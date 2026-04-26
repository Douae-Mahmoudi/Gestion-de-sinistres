package com.SinistraPro.domain.exposition.controller;


import com.SinistraPro.domain.model.StatutSinistre;
import com.SinistraPro.domain.port.out.SinistreRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
public class StatsController {

    private final SinistreRepositoryPort sinistreRepository;

    @GetMapping("/sinistres-par-statut")
    public ResponseEntity<Map<String, Long>> parStatut() {
        Map<String, Long> stats = new HashMap<>();
        for (StatutSinistre statut : StatutSinistre.values()) {
            stats.put(statut.name(),
                    (long) sinistreRepository.findByStatut(statut).size());
        }
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/resume")
    public ResponseEntity<Map<String, Object>> resume() {
        Map<String, Object> resume = new HashMap<>();

        long total = sinistreRepository.findAll().size();
        long approuves = sinistreRepository
                .findByStatut(StatutSinistre.APPROUVE).size();
        long rejetes = sinistreRepository
                .findByStatut(StatutSinistre.REJETE).size();
        long clotures = sinistreRepository
                .findByStatut(StatutSinistre.CLOTURE).size();
        long enCours = total - approuves - rejetes - clotures;

        resume.put("total", total);
        resume.put("enCours", enCours);
        resume.put("approuves", approuves);
        resume.put("rejetes", rejetes);
        resume.put("clotures", clotures);

        return ResponseEntity.ok(resume);
    }
}
