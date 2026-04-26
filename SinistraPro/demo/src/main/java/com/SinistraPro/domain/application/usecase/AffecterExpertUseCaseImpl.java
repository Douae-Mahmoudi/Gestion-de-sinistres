package com.SinistraPro.domain.application.usecase;

import com.SinistraPro.domain.application.service.NotificationService;
import com.SinistraPro.domain.model.*;
import com.SinistraPro.domain.port.in.AffecterExpertUseCase;
import com.SinistraPro.domain.port.out.HistoriqueRepositoryPort;
import com.SinistraPro.domain.port.out.SinistreRepositoryPort;
import com.SinistraPro.domain.port.out.UtilisateurRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AffecterExpertUseCaseImpl implements AffecterExpertUseCase {

    private final SinistreRepositoryPort sinistreRepository;
    private final UtilisateurRepositoryPort utilisateurRepository;
    private final HistoriqueRepositoryPort historiqueRepository;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public Sinistre affecter(Long sinistreId, Long expertId, String commentaireAgent, Utilisateur agentConnecte) {

        Sinistre sinistre = sinistreRepository.findById(sinistreId)
                .orElseThrow(() -> new IllegalArgumentException("Sinistre introuvable ID: " + sinistreId));

        Utilisateur expert = utilisateurRepository.findById(expertId)
                .orElseThrow(() -> new IllegalArgumentException("Expert introuvable ID: " + expertId));

        if (expert.getRole() != Role.EXPERT) {
            throw new IllegalStateException("L'utilisateur " + expert.getNom() + " n'est pas un expert qualifié");
        }

        StatutSinistre ancienStatut = sinistre.getStatut();

        //Mise à jour de l'état
        sinistre.validerTransition(StatutSinistre.AFFECTE);
        sinistre.setExpert(expert);
        sinistre.setAgent(agentConnecte);

        Sinistre saved = sinistreRepository.save(sinistre);

        // Historique
        Historique historique = Historique.builder()
                .sinistreId(sinistreId)
                .ancienStatut(ancienStatut)
                .nouveauStatut(StatutSinistre.AFFECTE)
                .commentaire(commentaireAgent)
                .effectuePar(agentConnecte)
                .dateAction(LocalDateTime.now())
                .build();

        historiqueRepository.save(historique);


        notificationService.creer(
                expert,
                saved,
                "Nouvelle mission : Vous avez été affecté au sinistre " + saved.getNumero(),
                "EXPERTISE"
        );

        if (saved.getClient() != null) {
            notificationService.creer(
                    saved.getClient(),
                    saved,
                    "Un expert (" + expert.getNom() + ") a été affecté à votre dossier " + saved.getNumero(),
                    "STATUT_CHANGE"
            );
        }

        return saved;
    }
}