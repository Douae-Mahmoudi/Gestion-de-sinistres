package com.SinistraPro.domain.model;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Historique {

    private Long id;
    private Long sinistreId;
    private StatutSinistre ancienStatut;
    private StatutSinistre nouveauStatut;
    private String commentaire;
    private Utilisateur effectuePar;
    private LocalDateTime dateAction;
}