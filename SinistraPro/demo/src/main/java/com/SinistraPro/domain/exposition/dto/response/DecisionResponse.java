package com.SinistraPro.domain.exposition.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DecisionResponse {
    private Long id;
    private BigDecimal montantFinal;
    private String statut;
    private String motif;
    private LocalDateTime dateDecision;
    private String numeroVirement;
    private LocalDate datePaiement;
    private UtilisateurResponse superviseur;
}
