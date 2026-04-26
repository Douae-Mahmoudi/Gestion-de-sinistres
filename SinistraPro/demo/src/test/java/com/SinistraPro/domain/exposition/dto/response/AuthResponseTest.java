package com.SinistraPro.domain.exposition.dto.response;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AuthResponseTest {

    @Test
    @DisplayName("Succès : Devrait créer une réponse d'authentification via le Builder")
    void shouldCreateAuthResponseWithBuilder() {
        String token = "jwt.token.here";
        String email = "expert@sinistrapro.com";
        String role = "EXPERT";
        String nomComplet = "Jean Expert";


        AuthResponse response = AuthResponse.builder()
                .token(token)
                .email(email)
                .role(role)
                .nomComplet(nomComplet)
                .build();

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo(token);
        assertThat(response.getEmail()).isEqualTo(email);
        assertThat(response.getRole()).isEqualTo(role);
        assertThat(response.getNomComplet()).isEqualTo(nomComplet);
    }

    @Test
    @DisplayName("Vérifie la présence des constructeurs requis par Jackson/Lombok")
    void shouldVerifyLombokConstructors() {
        AuthResponse empty = new AuthResponse();
        assertThat(empty).isNotNull();

        AuthResponse full = new AuthResponse("tk", "e@e.com", "ADMIN", "Admin User");
        assertThat(full.getToken()).isEqualTo("tk");
        assertThat(full.getRole()).isEqualTo("ADMIN");
    }

    @Test
    @DisplayName("Vérifie que les Getters fonctionnent correctement")
    void shouldVerifyGetters() {
        AuthResponse response = AuthResponse.builder()
                .email("test@test.com")
                .build();

        // Then
        assertThat(response.getEmail()).isEqualTo("test@test.com");
    }
}