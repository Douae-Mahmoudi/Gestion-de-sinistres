package com.SinistraPro.domain.exposition.dto.request;


import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class RapportExpertRequest {

    @NotBlank(message = "La description des dommages est obligatoire")
    private String descriptionDommages;

    @NotNull(message = "Le montant estimé est obligatoire")
    @DecimalMin(value = "0.0", inclusive = false,
            message = "Le montant estimé doit être positif")
    private BigDecimal montantEstime;

    private String observations;
}