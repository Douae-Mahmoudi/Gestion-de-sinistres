package com.SinistraPro.domain.model;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Utilisateur {

    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private String motDePasse;
    private String telephone;
    private Role role;
    private LocalDateTime dateCreation;
    private String resetToken;
    private String adresse;
    private LocalDateTime resetTokenExpiry;

    public String getNomComplet() {
        return nom + " " + prenom;
    }
    public String getRoleAsString() {
        return role != null ? role.name() : "";
    }
}