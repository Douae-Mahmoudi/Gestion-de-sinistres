package com.SinistraPro.domain.exposition.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateProfilRequest {
    @NotBlank private String nom;
    @NotBlank private String prenom;
    private String telephone;
    private String adresse;
}