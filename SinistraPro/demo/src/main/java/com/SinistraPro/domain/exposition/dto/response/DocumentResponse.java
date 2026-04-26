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
public class DocumentResponse {
    private Long id;
    private String nomFichier;
    private String typeDocument;
    private Long taille;
    private LocalDateTime dateUpload;
    private UtilisateurResponse uploadePar;
}