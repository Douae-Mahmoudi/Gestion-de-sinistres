package com.SinistraPro.domain.application.usecase;

import com.SinistraPro.domain.model.*;
import com.SinistraPro.domain.port.in.SoumettreRapportUseCase;
import com.SinistraPro.domain.port.out.HistoriqueRepositoryPort;
import com.SinistraPro.domain.port.out.RapportRepositoryPort;
import com.SinistraPro.domain.port.out.SinistreRepositoryPort;
import com.SinistraPro.domain.port.out.UtilisateurRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class SoumettreRapportUseCaseImpl implements SoumettreRapportUseCase {

    private final SinistreRepositoryPort sinistreRepository;
    private final RapportRepositoryPort rapportRepository;
    private final HistoriqueRepositoryPort historiqueRepository;
    private final UtilisateurRepositoryPort utilisateurRepository;

    @Override
    public Sinistre soumettre(Long sinistreId, Rapport rapport) {

        String emailAuthentifie = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        log.info("=== AUTH: email du token JWT = [{}]", emailAuthentifie);

        Utilisateur expertAuthentifie = utilisateurRepository.findByEmail(emailAuthentifie)
                .orElseThrow(() -> new IllegalStateException("Utilisateur authentifié introuvable"));

        log.info("=== AUTH: ID expert authentifié = [{}]", expertAuthentifie.getId());

        Sinistre sinistre = sinistreRepository.findById(sinistreId)
                .orElseThrow(() -> new IllegalArgumentException("Sinistre introuvable"));

        log.info("=== SINISTRE: expert_id en BDD = [{}]",
                sinistre.getExpert() != null ? sinistre.getExpert().getId() : "NULL");

        if (sinistre.getExpert() == null) {
            throw new IllegalStateException("Aucun expert affecté à ce sinistre");
        }

        if (!sinistre.getExpert().getId().equals(expertAuthentifie.getId())) {
            log.error("=== MISMATCH: sinistre.expert.id=[{}] vs auth.id=[{}]",
                    sinistre.getExpert().getId(), expertAuthentifie.getId());
            throw new IllegalStateException("Seul l'expert affecté peut soumettre le rapport");
        }

        StatutSinistre ancienStatut = sinistre.getStatut();

        rapport.setExpert(expertAuthentifie);
        rapport.setDateSoumission(LocalDateTime.now());

        sinistre.validerTransition(StatutSinistre.EN_EXPERTISE);

        Rapport savedRapport = rapportRepository.save(rapport);
        sinistre.setRapport(savedRapport);

        sinistre.validerTransition(StatutSinistre.EVALUE);

        Sinistre saved = sinistreRepository.save(sinistre);

        historiqueRepository.save(Historique.builder()
                .sinistreId(sinistreId)
                .ancienStatut(ancienStatut)
                .nouveauStatut(StatutSinistre.EVALUE)
                .commentaire("Rapport soumis. Montant estimé : "
                        + rapport.getMontantEstime() + " MAD")
                .effectuePar(expertAuthentifie)
                .dateAction(LocalDateTime.now())
                .build());

        return saved;
    }
}