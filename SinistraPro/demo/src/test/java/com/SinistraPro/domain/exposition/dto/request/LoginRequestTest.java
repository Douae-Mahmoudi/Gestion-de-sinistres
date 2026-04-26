package com.SinistraPro.domain.exposition.dto.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class LoginRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Succès : Le DTO est valide avec un email et un mot de passe corrects")
    void shouldBeValidWithCorrectData() {
        // Given
        LoginRequest request = new LoginRequest();
        request.setEmail("agent.test@sinistrapro.com");
        request.setMotDePasse("Password123!");

        // When
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Échec : Le mot de passe est absent (@NotBlank)")
    void shouldFailWhenPasswordIsBlank() {
        // Given
        LoginRequest request = new LoginRequest();
        request.setEmail("user@test.com");
        request.setMotDePasse(""); // Vide

        // When
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).isNotEmpty();
        boolean hasPasswordError = violations.stream()
                .anyMatch(v -> v.getMessage().equals("Le mot de passe est obligatoire"));
        assertThat(hasPasswordError).isTrue();
    }

    @Test
    @DisplayName("Échec : L'email est vide (@NotBlank)")
    void shouldFailWhenEmailIsBlank() {
        // Given
        LoginRequest request = new LoginRequest();
        request.setEmail("  "); // Espaces uniquement
        request.setMotDePasse("somePassword");

        // When
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).isNotEmpty();
        boolean hasEmailBlankError = violations.stream()
                .anyMatch(v -> v.getMessage().equals("L'email est obligatoire"));
        assertThat(hasEmailBlankError).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {"invalid-email", "user@", "@domain.com"})
    @DisplayName("Échec : Format email invalide (@Email)")
    void shouldFailWithInvalidEmailFormat(String invalidEmail) {
        // Given
        LoginRequest request = new LoginRequest();
        request.setEmail(invalidEmail);
        request.setMotDePasse("password");

        // When
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).isNotEmpty();
        boolean hasFormatError = violations.stream()
                .anyMatch(v -> v.getMessage().equals("Format email invalide"));
        assertThat(hasFormatError).isTrue();
    }

    @Test
    @DisplayName("Vérifie les Getters/Setters de Lombok")
    void shouldTestLombokAccessors() {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@test.fr");
        request.setMotDePasse("secret");

        assertThat(request.getEmail()).isEqualTo("test@test.fr");
        assertThat(request.getMotDePasse()).isEqualTo("secret");
    }
}