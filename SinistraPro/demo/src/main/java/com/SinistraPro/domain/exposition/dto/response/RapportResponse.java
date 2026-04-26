package com.SinistraPro.domain.exposition.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RapportResponse {
    private Long id;
    private String descriptionDommages;
    private BigDecimal montantEstime;
    private String observations;
    private LocalDateTime dateSoumission;
    private UtilisateurResponse expert;
}