package com.SinistraPro.domain.model;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Sinistre {

    private Long id;
    private String numero;
    private String typeSinistre;
    private String description;
    private LocalDate dateIncident;
    private String lieuIncident;
    private String numeroPolicAssurance;
    private String numeroConstatAmiable;
    private StatutSinistre statut;
    private LocalDateTime dateDeclaration;
    private Utilisateur client;
    private Utilisateur agent;
    private Utilisateur expert;
    private Rapport rapport;
    private Decision decision;


    public void validerTransition(StatutSinistre nouveauStatut) {
        if (this.statut == nouveauStatut) {
            return;
        }

        switch (this.statut) {
            case DECLARE -> {
                if (nouveauStatut != StatutSinistre.AFFECTE)
                    throw new IllegalStateException("Un sinistre déclaré ne peut qu'être affecté.");
            }
            case AFFECTE -> {
                if (nouveauStatut != StatutSinistre.EN_EXPERTISE)
                    throw new IllegalStateException("Un sinistre affecté ne peut qu'être mis en expertise.");
            }
            case EN_EXPERTISE -> {
                if (nouveauStatut != StatutSinistre.EVALUE)
                    throw new IllegalStateException("Un sinistre en expertise ne peut qu'être évalué.");
            }
            case EVALUE -> {
                if (nouveauStatut != StatutSinistre.APPROUVE && nouveauStatut != StatutSinistre.REJETE)
                    throw new IllegalStateException("Un sinistre évalué doit être approuvé ou rejeté.");
            }
            case APPROUVE -> {
                if (nouveauStatut != StatutSinistre.CLOTURE)
                    throw new IllegalStateException("Un sinistre approuvé ne peut qu'être clôturé.");
            }
            case REJETE, CLOTURE -> {
                throw new IllegalStateException("Impossible de modifier le statut d'un sinistre terminé (" + this.statut + ").");
            }
            default -> throw new IllegalStateException("Transition non autorisée depuis le statut : " + this.statut);
        }

        this.statut = nouveauStatut;
    }
}