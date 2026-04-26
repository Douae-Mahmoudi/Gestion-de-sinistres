package com.SinistraPro.domain.model;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Rapport {

    private Long id;
    private String descriptionDommages;
    private BigDecimal montantEstime;
    private String observations;
    private LocalDateTime dateSoumission;
    private Utilisateur expert;
}