package com.SinistraPro.domain.exposition.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPasswordRequest {
    @NotBlank
    private String email;

    @NotBlank
    @Size(min = 6, max = 6)
    private String code;

    @NotBlank
    @Size(min = 6, message = "Minimum 6 caractères")
    private String nouveauMotDePasse;
}