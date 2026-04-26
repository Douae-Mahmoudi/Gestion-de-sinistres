package com.SinistraPro.domain.exposition.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class DeclarationSinistreRequest {

    @NotBlank(message = "Le type de sinistre est obligatoire")
    private String typeSinistre;

    @NotBlank(message = "La description est obligatoire")
    private String description;

    @NotNull(message = "La date d'incident est obligatoire")
    @PastOrPresent(message = "La date d'incident ne peut pas être dans le futur")
    private LocalDate dateIncident;

    @NotBlank(message = "Le lieu d'incident est obligatoire")
    private String lieuIncident;

    @NotBlank(message = "Le numéro de police d'assurance est obligatoire")
    private String numeroPolicAssurance;

    @NotBlank(message = "Le numéro de constat amiable est obligatoire")
    private String numeroConstatAmiable;
}
