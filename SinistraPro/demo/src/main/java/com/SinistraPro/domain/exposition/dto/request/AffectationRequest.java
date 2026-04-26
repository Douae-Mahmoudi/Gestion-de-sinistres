package com.SinistraPro.domain.exposition.dto.request;


import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AffectationRequest {

    @NotNull(message = "L'identifiant de l'expert est obligatoire")
    private Long expertId;

    private String commentaireAgent;
}
