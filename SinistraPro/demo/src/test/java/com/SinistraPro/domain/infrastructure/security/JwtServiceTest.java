package com.SinistraPro.domain.infrastructure.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    private JwtService jwtService;

    private final String testSecret = "mySecretKeyForTestingPurposesOnlyMustBeLongEnough";
    private final long testExpiration = 3600000;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secret", testSecret);
        ReflectionTestUtils.setField(jwtService, "expiration", testExpiration);
    }

    @Test
    @DisplayName("Succès : Génération et extraction des données du token")
    void shouldGenerateAndExtractDataFromToken() {
        String email = "expert@sinistra.pro";
        String role = "EXPERT";

        String token = jwtService.generateToken(email, role);

        assertThat(token).isNotBlank();
        assertThat(jwtService.extractEmail(token)).isEqualTo(email);
        assertThat(jwtService.extractRole(token)).isEqualTo(role);
    }

    @Test
    @DisplayName("Succès : Le token généré doit être valide immédiatement")
    void shouldValidateCorrectToken() {
        String token = jwtService.generateToken("user@test.com", "CLIENT");

        boolean isValid = jwtService.isTokenValid(token);

        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("Échec : Un token expiré ou corrompu doit être invalide")
    void shouldReturnFalseForInvalidOrExpiredToken() {
        String invalidToken = "this.is.not.a.valid.token";

        boolean isValid = jwtService.isTokenValid(invalidToken);

        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("Échec : Validation d'un token expiré")
    void shouldReturnFalseWhenTokenIsExpired() {
        ReflectionTestUtils.setField(jwtService, "expiration", -1000L);
        String token = jwtService.generateToken("expired@user.com", "AGENT");

        boolean isValid = jwtService.isTokenValid(token);

        assertThat(isValid).isFalse();
    }
}