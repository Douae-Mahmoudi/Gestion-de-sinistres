package com.SinistraPro.domain.exposition.mapper;

import com.SinistraPro.domain.model.*;
import com.SinistraPro.domain.exposition.dto.response.*;
import org.springframework.stereotype.Component;

@Component
public class SinistreDtoMapper {

    public SinistreResponse toResponse(Sinistre s) {
        if (s == null) return null;

        String nomAssure = "Client Inconnu";
        if (s.getClient() != null) {
            nomAssure = s.getClient().getNomComplet() != null ?
                    s.getClient().getNomComplet() :
                    (s.getClient().getNom() + " " + s.getClient().getPrenom());
        }

        return SinistreResponse.builder()
                .id(s.getId())
                .numero(s.getNumero())
                .typeSinistre(s.getTypeSinistre())
                .description(s.getDescription())
                .dateIncident(s.getDateIncident())

                .lieuIncident(s.getLieuIncident() != null ? s.getLieuIncident() : "Non spécifiée")
                .localisation(s.getLieuIncident() != null ? s.getLieuIncident() : "Non spécifiée")

                .nomAssure(nomAssure)

                .numeroPolicAssurance(s.getNumeroPolicAssurance())
                .numeroConstatAmiable(s.getNumeroConstatAmiable())
                .statut(s.getStatut() != null ? s.getStatut().name() : null)
                .dateDeclaration(s.getDateDeclaration())

                .client(toUtilisateurResponse(s.getClient()))
                .agent(toUtilisateurResponse(s.getAgent()))
                .expert(toUtilisateurResponse(s.getExpert()))
                .rapport(toRapportResponse(s.getRapport()))
                .decision(toDecisionResponse(s.getDecision()))
                .build();
    }

    public UtilisateurResponse toUtilisateurResponse(Utilisateur u) {
        if (u == null) return null;

        String nomComplet = u.getNomComplet();
        if (nomComplet == null || nomComplet.isBlank()) {
            nomComplet = (u.getNom() != null ? u.getNom() : "") + " " + (u.getPrenom() != null ? u.getPrenom() : "");
        }

        return UtilisateurResponse.builder()
                .id(u.getId())
                .nom(u.getNom())
                .prenom(u.getPrenom())
                .nomComplet(nomComplet.trim())
                .email(u.getEmail())
                .telephone(u.getTelephone())
                .role(u.getRole() != null ? u.getRole().name() : null)
                .dateCreation(u.getDateCreation())
                .build();
    }

    public RapportResponse toRapportResponse(Rapport r) {
        if (r == null) return null;
        return RapportResponse.builder()
                .id(r.getId())
                .descriptionDommages(r.getDescriptionDommages())
                .montantEstime(r.getMontantEstime())
                .observations(r.getObservations())
                .dateSoumission(r.getDateSoumission())
                .expert(toUtilisateurResponse(r.getExpert()))
                .build();
    }

    public DecisionResponse toDecisionResponse(Decision d) {
        if (d == null) return null;
        return DecisionResponse.builder()
                .id(d.getId())
                .montantFinal(d.getMontantFinal())
                .statut(d.getStatut() != null ? d.getStatut().name() : null)
                .motif(d.getMotif())
                .dateDecision(d.getDateDecision())
                .numeroVirement(d.getNumeroVirement())
                .datePaiement(d.getDatePaiement())
                .superviseur(toUtilisateurResponse(d.getSuperviseur()))
                .build();
    }

    public DocumentResponse toDocumentResponse(Document doc) {
        if (doc == null) return null;
        return DocumentResponse.builder()
                .id(doc.getId())
                .nomFichier(doc.getNomFichier())
                .typeDocument(doc.getTypeDocument())
                .taille(doc.getTaille())
                .dateUpload(doc.getDateUpload())
                .uploadePar(toUtilisateurResponse(doc.getUploadePar()))
                .build();
    }

    public HistoriqueResponse toHistoriqueResponse(Historique h) {
        if (h == null) return null;
        return HistoriqueResponse.builder()
                .id(h.getId())
                .ancienStatut(h.getAncienStatut() != null ? h.getAncienStatut().name() : null)
                .nouveauStatut(h.getNouveauStatut() != null ? h.getNouveauStatut().name() : null)
                .commentaire(h.getCommentaire())
                .effectuePar(toUtilisateurResponse(h.getEffectuePar()))
                .dateAction(h.getDateAction())
                .build();
    }
}