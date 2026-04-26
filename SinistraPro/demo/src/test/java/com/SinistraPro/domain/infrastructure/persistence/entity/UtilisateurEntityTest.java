package com.SinistraPro.domain.infrastructure.persistence.entity;

import com.SinistraPro.domain.model.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class UtilisateurEntityTest {

    @Test
    @DisplayName("Vérification du fonctionnement du Builder et des champs de sécurité")
    void testUtilisateurEntity() {
        LocalDateTime now = LocalDateTime.now();

        UtilisateurEntity user = UtilisateurEntity.builder()
                .id(1L)
                .nom("Benani")
                .prenom("Sami")
                .email("sami@expert.ma")
                .motDePasse("hashed_pwd")
                .role(Role.EXPERT)
                .dateCreation(now)
                .resetToken("token-xyz")
                .build();

        assertThat(user.getEmail()).isEqualTo("sami@expert.ma");
        assertThat(user.getRole()).isEqualTo(Role.EXPERT);
        assertThat(user.getResetToken()).isEqualTo("token-xyz");
        assertThat(user.getDateCreation()).isEqualTo(now);
    }
}