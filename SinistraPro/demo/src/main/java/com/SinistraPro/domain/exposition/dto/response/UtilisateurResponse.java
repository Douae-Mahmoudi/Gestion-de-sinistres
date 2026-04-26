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
public class UtilisateurResponse {
    private Long id;
    private String nom;
    private String prenom;
    private String nomComplet;
    private String email;
    private String telephone;
    private String role;
    private LocalDateTime dateCreation;
    private String adresse;
}
