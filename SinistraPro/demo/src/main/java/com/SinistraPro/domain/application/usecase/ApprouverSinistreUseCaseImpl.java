package com.SinistraPro.domain.application.usecase;

import com.SinistraPro.domain.model.*;
import com.SinistraPro.domain.port.in.ApprouverSinistreUseCase;
import com.SinistraPro.domain.port.out.DecisionRepositoryPort;
import com.SinistraPro.domain.port.out.HistoriqueRepositoryPort;
import com.SinistraPro.domain.port.out.SinistreRepositoryPort;
import com.SinistraPro.domain.port.out.UtilisateurRepositoryPort;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ApprouverSinistreUseCaseImpl implements ApprouverSinistreUseCase {

    private static final BigDecimal SEUIL_DOUBLE_VALIDATION = new BigDecimal("50000");

    private final SinistreRepositoryPort sinistreRepository;
    private final UtilisateurRepositoryPort utilisateurRepository;
    private final DecisionRepositoryPort decisionRepository;
    private final HistoriqueRepositoryPort historiqueRepository;

    @Override
    @Transactional

    public Sinistre approuver(Long sinistreId, BigDecimal montantFinal,
                              String motif, Long superviseurId) {

        Sinistre sinistre = sinistreRepository.findById(sinistreId)
                .orElseThrow(() -> new IllegalArgumentException("Sinistre introuvable"));

        Utilisateur superviseur = utilisateurRepository.findById(superviseurId)
                .orElseThrow(() -> new IllegalArgumentException("Superviseur introuvable"));

        if (superviseur.getRole() != Role.SUPERVISEUR) {
            throw new IllegalStateException("Seul un superviseur peut approuver");
        }

        if (montantFinal.compareTo(SEUIL_DOUBLE_VALIDATION) > 0) {

            Decision decisionExistante = decisionRepository
                    .findBySinistreId(sinistreId).orElse(null);

            if (decisionExistante == null) {
                Decision premiereValidation = Decision.builder()
                        .montantFinal(montantFinal)
                        .statut(StatutDecision.APPROUVE)
                        .motif(motif + " [EN ATTENTE DOUBLE VALIDATION]")
                        .dateDecision(LocalDateTime.now())
                        .superviseur(superviseur)
                        .build();
                decisionRepository.save(premiereValidation);

                historiqueRepository.save(Historique.builder()
                        .sinistreId(sinistreId)
                        .ancienStatut(sinistre.getStatut())
                        .nouveauStatut(sinistre.getStatut())
                        .commentaire("Première validation superviseur — montant > 50 000 MAD, "
                                + "double validation requise")
                        .effectuePar(superviseur)
                        .dateAction(LocalDateTime.now())
                        .build());

                return sinistre;
            }
        }

        StatutSinistre ancienStatut = sinistre.getStatut();
        sinistre.validerTransition(StatutSinistre.APPROUVE);

        Decision decision = Decision.builder()
                .montantFinal(montantFinal)
                .statut(StatutDecision.APPROUVE)
                .motif(motif)
                .dateDecision(LocalDateTime.now())
                .superviseur(superviseur)
                .build();

        Decision savedDecision = decisionRepository.save(decision);
        sinistre.setDecision(savedDecision);

        Sinistre saved = sinistreRepository.save(sinistre);

        historiqueRepository.save(Historique.builder()
                .sinistreId(sinistreId)
                .ancienStatut(ancienStatut)
                .nouveauStatut(StatutSinistre.APPROUVE)
                .commentaire("Sinistre approuvé. Montant final : " + montantFinal + " MAD")
                .effectuePar(superviseur)
                .dateAction(LocalDateTime.now())
                .build());

        return saved;
    }
}