package com.SinistraPro.domain.application.usecase;

import com.SinistraPro.domain.application.service.NotificationService;
import com.SinistraPro.domain.model.*;
import com.SinistraPro.domain.port.in.DeclarerSinistreUseCase;
import com.SinistraPro.domain.port.out.HistoriqueRepositoryPort;
import com.SinistraPro.domain.port.out.SinistreRepositoryPort;
import com.SinistraPro.domain.port.out.UtilisateurRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DeclarerSinistreUseCaseImpl implements DeclarerSinistreUseCase {

    private final SinistreRepositoryPort sinistreRepository;
    private final UtilisateurRepositoryPort utilisateurRepository;
    private final HistoriqueRepositoryPort historiqueRepository;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public Sinistre declarer(Sinistre sinistre) {
        Utilisateur client = utilisateurRepository.findById(sinistre.getClient().getId())
                .orElseThrow(() -> new IllegalArgumentException("Client introuvable"));

        if (client.getRole() != Role.CLIENT) {
            throw new IllegalStateException("Seul un client peut déclarer un sinistre");
        }

        sinistre.setNumero(genererNumeroDossier());
        sinistre.setStatut(StatutSinistre.DECLARE);
        sinistre.setDateDeclaration(LocalDateTime.now());
        sinistre.setClient(client);

        Sinistre saved = sinistreRepository.save(sinistre);

        historiqueRepository.save(Historique.builder()
                .sinistreId(saved.getId())
                .nouveauStatut(StatutSinistre.DECLARE)
                .commentaire("Sinistre déclaré par le client")
                .effectuePar(client)
                .dateAction(LocalDateTime.now())
                .build());

        notificationService.creer(
                client,
                saved,
                "Votre déclaration " + saved.getNumero() + " a été transmise avec succès.",
                "STATUT_CHANGE"
        );

        // On récupère la liste des agents pour les prévenir du nouveau dossier
        List<Utilisateur> agents = utilisateurRepository.findByRole(Role.AGENT);
        for (Utilisateur agent : agents) {
            notificationService.creer(
                    agent,
                    saved,
                    "Nouveau sinistre à traiter : " + saved.getNumero() + " (Client: " + client.getNom() + ")",
                    "NOUVELLE_DECLARATION"
            );
        }

        return saved;
    }

    private String genererNumeroDossier() {
        String annee = String.valueOf(LocalDateTime.now().getYear());
        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("MMddHHmmss"));
        return "SIN-" + annee + "-" + timestamp;
    }
}