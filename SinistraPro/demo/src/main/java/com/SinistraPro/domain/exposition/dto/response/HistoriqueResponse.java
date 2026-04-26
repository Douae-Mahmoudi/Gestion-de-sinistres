package com.SinistraPro.domain.exposition.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HistoriqueResponse {
    private Long id;
    private String ancienStatut;
    private String nouveauStatut;
    private String commentaire;
    private UtilisateurResponse effectuePar;
    private LocalDateTime dateAction;
}