package com.SinistraPro.domain.model;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Decision {

    private Long id;
    private BigDecimal montantFinal;
    private StatutDecision statut;
    private String motif;
    private LocalDateTime dateDecision;
    private String numeroVirement;
    private LocalDate datePaiement;
    private Utilisateur superviseur;
}