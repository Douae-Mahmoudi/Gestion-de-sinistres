package com.SinistraPro.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class UtilisateurTest {

    @Test
    @DisplayName("Devrait créer un Utilisateur avec le Builder et vérifier les champs")
    void shouldCreateUtilisateurWithBuilder() {
        LocalDateTime now = LocalDateTime.now();

        Utilisateur utilisateur = Utilisateur.builder()
                .id(1L)
                .nom("Doe")
                .prenom("John")
                .email("john.doe@example.com")
                .motDePasse("secret123")
                .role(Role.AGENT)
                .dateCreation(now)
                .build();

        // Then
        assertThat(utilisateur).isNotNull();
        assertThat(utilisateur.getId()).isEqualTo(1L);
        assertThat(utilisateur.getNom()).isEqualTo("Doe");
        assertThat(utilisateur.getPrenom()).isEqualTo("John");
        assertThat(utilisateur.getEmail()).isEqualTo("john.doe@example.com");
        assertThat(utilisateur.getRole()).isEqualTo(Role.AGENT);
        assertThat(utilisateur.getDateCreation()).isEqualTo(now);
    }

    @Test
    @DisplayName("Devrait retourner le nom complet correctement concaténé")
    void shouldReturnCorrectFullName() {
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setNom("Dupont");
        utilisateur.setPrenom("Jean");

        String nomComplet = utilisateur.getNomComplet();

        assertThat(nomComplet).isEqualTo("Dupont Jean");
    }

    @Test
    @DisplayName("Devrait gérer les tokens de réinitialisation de mot de passe")
    void shouldHandleResetTokenFields() {
        Utilisateur utilisateur = new Utilisateur();
        LocalDateTime expiry = LocalDateTime.now().plusHours(2);
        String token = "abc-123-xyz";

        utilisateur.setResetToken(token);
        utilisateur.setResetTokenExpiry(expiry);

        // Then
        assertThat(utilisateur.getResetToken()).isEqualTo(token);
        assertThat(utilisateur.getResetTokenExpiry()).isEqualTo(expiry);
    }

    @Test
    @DisplayName("Vérifie les constructeurs par défaut et complets")
    void shouldVerifyLombokConstructors() {
        Utilisateur empty = new Utilisateur();
        assertThat(empty).isNotNull();

        Utilisateur full = new Utilisateur(1L, "Nom", "Prenom", "e@e.com", "pass", "0123", Role.CLIENT,null,null, null, null);
        assertThat(full.getNom()).isEqualTo("Nom");
        assertThat(full.getRole()).isEqualTo(Role.CLIENT);
    }
}