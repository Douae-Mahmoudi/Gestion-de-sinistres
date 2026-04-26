package com.SinistraPro.domain.exposition.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VerifierCodeRequest {
    @NotBlank
    private String email;

    @NotBlank
    @Size(min = 6, max = 6, message = "Le code doit contenir 6 chiffres")
    private String code;
}