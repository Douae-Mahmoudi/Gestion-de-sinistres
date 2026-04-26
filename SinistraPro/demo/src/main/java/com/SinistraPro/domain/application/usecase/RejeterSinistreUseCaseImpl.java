package com.SinistraPro.domain.application.usecase;


import com.SinistraPro.domain.model.*;
import com.SinistraPro.domain.port.in.RejeterSinistreUseCase;
import com.SinistraPro.domain.port.out.DecisionRepositoryPort;
import com.SinistraPro.domain.port.out.HistoriqueRepositoryPort;
import com.SinistraPro.domain.port.out.SinistreRepositoryPort;
import com.SinistraPro.domain.port.out.UtilisateurRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RejeterSinistreUseCaseImpl implements RejeterSinistreUseCase {

    private final SinistreRepositoryPort sinistreRepository;
    private final UtilisateurRepositoryPort utilisateurRepository;
    private final DecisionRepositoryPort decisionRepository;
    private final HistoriqueRepositoryPort historiqueRepository;

    @Override
    public Sinistre rejeter(Long sinistreId, String motif, Long superviseurId) {
        Sinistre sinistre = sinistreRepository.findById(sinistreId)
                .orElseThrow(() -> new IllegalArgumentException("Sinistre introuvable"));

        Utilisateur superviseur = utilisateurRepository.findById(superviseurId)
                .orElseThrow(() -> new IllegalArgumentException("Superviseur introuvable"));

        if (superviseur.getRole() != Role.SUPERVISEUR) {
            throw new IllegalStateException("Seul un superviseur peut rejeter");
        }

        StatutSinistre ancienStatut = sinistre.getStatut();
        sinistre.validerTransition(StatutSinistre.REJETE);

        Decision decision = Decision.builder()
                .statut(StatutDecision.REJETE)
                .motif(motif)
                .dateDecision(LocalDateTime.now())
                .superviseur(superviseur)
                .build();

        decisionRepository.save(decision);
        sinistre.setDecision(decision);

        Sinistre saved = sinistreRepository.save(sinistre);

        historiqueRepository.save(Historique.builder()
                .sinistreId(sinistreId)
                .ancienStatut(ancienStatut)
                .nouveauStatut(StatutSinistre.REJETE)
                .commentaire("Rejeté. Motif : " + motif)
                .effectuePar(superviseur)
                .dateAction(LocalDateTime.now())
                .build());

        return saved;
    }
}