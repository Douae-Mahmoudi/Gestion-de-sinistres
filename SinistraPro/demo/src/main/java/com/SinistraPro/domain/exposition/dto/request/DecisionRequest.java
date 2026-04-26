package com.SinistraPro.domain.exposition.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class DecisionRequest {

    // Pour approuver
    @DecimalMin(value = "0.0", inclusive = false,
            message = "Le montant doit être positif")
    private BigDecimal montantFinal;

    // Pour approuver et rejeter
    @NotBlank(message = "Le motif est obligatoire")
    private String motif;

    // Pour clôturer
    private String numeroVirement;
    private LocalDate datePaiement;
}