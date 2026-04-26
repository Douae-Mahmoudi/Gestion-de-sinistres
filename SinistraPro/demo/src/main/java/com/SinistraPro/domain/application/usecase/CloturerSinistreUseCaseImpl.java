package com.SinistraPro.domain.application.usecase;


import com.SinistraPro.domain.model.*;
import com.SinistraPro.domain.port.in.CloturerSinistreUseCase;
import com.SinistraPro.domain.port.out.DecisionRepositoryPort;
import com.SinistraPro.domain.port.out.HistoriqueRepositoryPort;
import com.SinistraPro.domain.port.out.SinistreRepositoryPort;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CloturerSinistreUseCaseImpl implements CloturerSinistreUseCase {

    private final SinistreRepositoryPort sinistreRepository;
    private final DecisionRepositoryPort decisionRepository;
    private final HistoriqueRepositoryPort historiqueRepository;

    @Override
    @Transactional

    public Sinistre cloturer(Long sinistreId, String numeroVirement,
                             java.time.LocalDate datePaiement) {
        Sinistre sinistre = sinistreRepository.findById(sinistreId)
                .orElseThrow(() -> new IllegalArgumentException("Sinistre introuvable"));

        if (sinistre.getDecision() == null ||
                sinistre.getDecision().getStatut() != StatutDecision.APPROUVE) {
            throw new IllegalStateException(
                    "Impossible de clôturer un sinistre sans décision approuvée");
        }

        // Enregistrer les infos de paiement sur la décision
        Decision decision = sinistre.getDecision();
        decision.setNumeroVirement(numeroVirement);
        decision.setDatePaiement(datePaiement);
        decisionRepository.save(decision);

        StatutSinistre ancienStatut = sinistre.getStatut();
        historiqueRepository.save(Historique.builder()
                .sinistreId(sinistreId)
                .ancienStatut(ancienStatut)
                .nouveauStatut(StatutSinistre.CLOTURE)
                .commentaire("Paiement confirmé. Virement n° " + numeroVirement
                        + " du " + datePaiement)
                .effectuePar(sinistre.getAgent())
                .dateAction(LocalDateTime.now())
                .build()); sinistre.validerTransition(StatutSinistre.CLOTURE);

        Sinistre saved = sinistreRepository.save(sinistre);



        return saved;
    }
}
