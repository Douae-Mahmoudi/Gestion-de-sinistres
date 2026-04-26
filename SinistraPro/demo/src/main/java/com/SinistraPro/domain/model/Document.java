package com.SinistraPro.domain.model;
import lombok.*;

import java.time.LocalDateTime;
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Document {

    private Long id;
    private String nomFichier;
    private String cheminFichier;
    private String typeDocument;
    private Long taille;
    private LocalDateTime dateUpload;
    private Utilisateur uploadePar;
}
