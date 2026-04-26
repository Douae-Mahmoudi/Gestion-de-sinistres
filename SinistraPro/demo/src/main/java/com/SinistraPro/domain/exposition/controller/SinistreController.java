package com.SinistraPro.domain.exposition.controller;

import com.SinistraPro.domain.model.*;
import com.SinistraPro.domain.port.in.*;
import com.SinistraPro.domain.port.out.SinistreRepositoryPort;
import com.SinistraPro.domain.port.out.UtilisateurRepositoryPort;
import com.SinistraPro.domain.exposition.dto.request.*;
import com.SinistraPro.domain.exposition.dto.response.SinistreResponse;
import com.SinistraPro.domain.exposition.mapper.SinistreDtoMapper;
import com.SinistraPro.domain.application.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/sinistres")
@RequiredArgsConstructor
public class SinistreController {

    private final DeclarerSinistreUseCase declarerUseCase;
    private final AffecterExpertUseCase affecterUseCase;
    private final SoumettreRapportUseCase soumettreRapportUseCase;
    private final ApprouverSinistreUseCase approuverUseCase;
    private final RejeterSinistreUseCase rejeterUseCase;
    private final CloturerSinistreUseCase cloturerUseCase;

    private final SinistreRepositoryPort sinistreRepository;
    private final UtilisateurRepositoryPort utilisateurRepository;
    private final SinistreDtoMapper dtoMapper;
    private final NotificationService notificationService;

    @PostMapping
    public ResponseEntity<SinistreResponse> declarer(
            @Valid @RequestBody DeclarationSinistreRequest request,
            Authentication auth) {

        Utilisateur client = getConnectedUser(auth);
        Sinistre sinistre = Sinistre.builder()
                .typeSinistre(request.getTypeSinistre())
                .description(request.getDescription())
                .dateIncident(request.getDateIncident())
                .lieuIncident(request.getLieuIncident())
                .numeroPolicAssurance(request.getNumeroPolicAssurance())
                .client(client)
                .build();

        Sinistre created = declarerUseCase.declarer(sinistre);

        notificationService.creer(
                client,
                created,
                "Votre sinistre #SP-" + created.getId() + " a bien été déclaré et est en cours de traitement.",
                "STATUT_CHANGE"
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(dtoMapper.toResponse(created));
    }

    @PutMapping("/{id}/affecter")
    public ResponseEntity<SinistreResponse> affecter(
            @PathVariable Long id,
            @Valid @RequestBody AffectationRequest request,
            Authentication auth) {

        Utilisateur agent = getConnectedUser(auth);
        Sinistre updated = affecterUseCase.affecter(
                id, request.getExpertId(), request.getCommentaireAgent(), agent);

        notificationService.creer(
                updated.getClient(),
                updated,
                "Un expert a été assigné à votre dossier #SP-" + id + ". L'expertise est en cours.",
                "EXPERTISE"
        );

        if (updated.getExpert() != null) {
            notificationService.creer(
                    updated.getExpert(),
                    updated,
                    "Nouvelle mission assignée : dossier #SP-" + id
                            + " (" + updated.getTypeSinistre() + ")"
                            + (updated.getLieuIncident() != null ? " — " + updated.getLieuIncident() : ""),
                    "EXPERTISE"
            );
        }

        return ResponseEntity.ok(dtoMapper.toResponse(updated));
    }

    @PutMapping("/{id}/soumettre-rapport")
    public ResponseEntity<SinistreResponse> soumettreRapport(
            @PathVariable Long id,
            @Valid @RequestBody RapportExpertRequest request,
            Authentication auth) {

        Utilisateur expert = getConnectedUser(auth);
        Rapport rapport = Rapport.builder()
                .descriptionDommages(request.getDescriptionDommages())
                .montantEstime(request.getMontantEstime())
                .observations(request.getObservations())
                .expert(expert)
                .build();

        Sinistre updated = soumettreRapportUseCase.soumettre(id, rapport);

        notificationService.creer(
                updated.getClient(),
                updated,
                "Le rapport d'expertise de votre dossier #SP-" + id + " est disponible.",
                "EXPERTISE"
        );

        utilisateurRepository.findByRole(Role.SUPERVISEUR).forEach(superviseur ->
                notificationService.creer(
                        superviseur,
                        updated,
                        "Le dossier #SP-" + id + " (" + updated.getTypeSinistre()
                                + ") est évalué et attend votre décision.",
                        "DECISION"
                )
        );

        return ResponseEntity.ok(dtoMapper.toResponse(updated));
    }

    @PutMapping("/{id}/approuver")
    public ResponseEntity<SinistreResponse> approuver(
            @PathVariable Long id,
            @Valid @RequestBody DecisionRequest request,
            Authentication auth) {

        Utilisateur superviseur = getConnectedUser(auth);
        Sinistre updated = approuverUseCase.approuver(
                id, request.getMontantFinal(), request.getMotif(), superviseur.getId());

        notificationService.creer(
                updated.getClient(),
                updated,
                "Félicitations ! Votre dossier #SP-" + id
                        + " a été approuvé. Montant accordé : " + request.getMontantFinal() + " MAD.",
                "DECISION"
        );

        if (updated.getExpert() != null) {
            notificationService.creer(
                    updated.getExpert(),
                    updated,
                    "Le dossier #SP-" + id + " que vous avez expertisé a été approuvé.",
                    "DECISION"
            );
        }

        return ResponseEntity.ok(dtoMapper.toResponse(updated));
    }

    @PutMapping("/{id}/rejeter")
    public ResponseEntity<SinistreResponse> rejeter(
            @PathVariable Long id,
            @Valid @RequestBody DecisionRequest request,
            Authentication auth) {

        Utilisateur superviseur = getConnectedUser(auth);
        Sinistre updated = rejeterUseCase.rejeter(
                id, request.getMotif(), superviseur.getId());

        notificationService.creer(
                updated.getClient(),
                updated,
                "Votre dossier #SP-" + id + " a été rejeté. Motif : " + request.getMotif(),
                "DECISION"
        );

        if (updated.getExpert() != null) {
            notificationService.creer(
                    updated.getExpert(),
                    updated,
                    "Le dossier #SP-" + id + " que vous avez expertisé a été rejeté.",
                    "DECISION"
            );
        }

        return ResponseEntity.ok(dtoMapper.toResponse(updated));
    }

    @PutMapping("/{id}/cloturer")
    @PreAuthorize("hasAuthority('ROLE_AGENT')")
    public ResponseEntity<SinistreResponse> cloturer(
            @PathVariable Long id,
            @Valid @RequestBody DecisionRequest request) {

        Sinistre updated = cloturerUseCase.cloturer(
                id, request.getNumeroVirement(), request.getDatePaiement());

        notificationService.creer(
                updated.getClient(),
                updated,
                "Votre dossier #SP-" + id
                        + " est clôturé. Le virement N°" + request.getNumeroVirement() + " a été effectué.",
                "CLOTURE"
        );

        return ResponseEntity.ok(dtoMapper.toResponse(updated));
    }

    @GetMapping("/expert/missions")
    public ResponseEntity<List<SinistreResponse>> getMissionsExpert(Authentication auth) {
        Utilisateur expert = getConnectedUser(auth);
        return ResponseEntity.ok(
                sinistreRepository.findByExpertId(expert.getId())
                        .stream()
                        .map(dtoMapper::toResponse)
                        .collect(Collectors.toList())
        );
    }

    @GetMapping("/stats/resume")
    public ResponseEntity<Map<String, Long>> getResumeStats(Authentication auth) {
        Utilisateur user = getConnectedUser(auth);
        Map<String, Long> stats = new HashMap<>();
        List<Sinistre> liste;

        if (Role.EXPERT.equals(user.getRole())) {
            liste = sinistreRepository.findByExpertId(user.getId());
            stats.put("missionsAttente",   liste.stream().filter(s -> s.getStatut() == StatutSinistre.AFFECTE).count());
            stats.put("expertisesEnCours", liste.stream().filter(s -> s.getStatut() == StatutSinistre.EN_EXPERTISE).count());
            stats.put("dossiersClotures",  liste.stream().filter(s -> s.getStatut() == StatutSinistre.CLOTURE).count());
        } else {
            liste = sinistreRepository.findByClientId(user.getId());
            stats.put("total",    (long) liste.size());
            stats.put("enCours",  liste.stream().filter(s -> s.getStatut() != StatutSinistre.CLOTURE && s.getStatut() != StatutSinistre.REJETE).count());
            stats.put("approuves",liste.stream().filter(s -> s.getStatut() == StatutSinistre.APPROUVE).count());
            stats.put("rejetes",  liste.stream().filter(s -> s.getStatut() == StatutSinistre.REJETE).count());
            stats.put("clotures", liste.stream().filter(s -> s.getStatut() == StatutSinistre.CLOTURE).count());
        }

        return ResponseEntity.ok(stats);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SinistreResponse> getById(@PathVariable Long id) {
        return sinistreRepository.findById(id)
                .map(dtoMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/mes-sinistres")
    public ResponseEntity<List<SinistreResponse>> getMesSinistres(Authentication auth) {
        Utilisateur client = getConnectedUser(auth);
        return ResponseEntity.ok(
                sinistreRepository.findByClientId(client.getId())
                        .stream()
                        .map(dtoMapper::toResponse)
                        .collect(Collectors.toList())
        );
    }

    @GetMapping
    public ResponseEntity<List<SinistreResponse>> getAll() {
        return ResponseEntity.ok(
                sinistreRepository.findAll()
                        .stream()
                        .map(dtoMapper::toResponse)
                        .collect(Collectors.toList())
        );
    }

    private Utilisateur getConnectedUser(Authentication auth) {
        return utilisateurRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé : " + auth.getName()));
    }
}
