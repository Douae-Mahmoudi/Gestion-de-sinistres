package com.SinistraPro.domain.exposition.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SinistreResponse {
    private Long id;
    private String numero;
    private String typeSinistre;
    private String description;
    private LocalDate dateIncident;
    private String lieuIncident;

    private String nomAssure;
    private String localisation;

    private String numeroPolicAssurance;
    private String numeroConstatAmiable;
    private String statut;
    private LocalDateTime dateDeclaration;

    private UtilisateurResponse client;
    private UtilisateurResponse agent;
    private UtilisateurResponse expert;
    private RapportResponse rapport;
    private DecisionResponse decision;
}