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

class ForgotPasswordRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Succès : Le DTO est valide avec un email correct")
    void shouldBeValidWithCorrectEmail() {
        // Given
        ForgotPasswordRequest request = new ForgotPasswordRequest();
        request.setEmail("contact@sinistrapro.com");

        // When
        Set<ConstraintViolation<ForgotPasswordRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Échec : L'email est vide (@NotBlank)")
    void shouldFailWhenEmailIsBlank() {
        // Given
        ForgotPasswordRequest request = new ForgotPasswordRequest();
        request.setEmail(""); // Vide

        // When
        Set<ConstraintViolation<ForgotPasswordRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).isNotEmpty();

        boolean hasNotBlankMessage = violations.stream()
                .anyMatch(v -> v.getMessage().equals("L'email est obligatoire"));

        assertThat(hasNotBlankMessage).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {"invalid-email", "test@com", "user@.com", "@domain.com"})
    @DisplayName("Échec : Le format de l'email est invalide (@Email)")
    void shouldFailWithInvalidEmailFormat(String invalidEmail) {
        // Given
        ForgotPasswordRequest request = new ForgotPasswordRequest();
        request.setEmail(invalidEmail);

        // When
        Set<ConstraintViolation<ForgotPasswordRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations)
                .as("L'email %s devrait être invalide", invalidEmail)
                .isNotEmpty();

        boolean hasEmailFormatError = violations.stream()
                .anyMatch(v -> v.getMessage().equals("Format email invalide"));
        assertThat(hasEmailFormatError).isTrue();
    }

    @Test
    @DisplayName("Vérifie les Getters/Setters Lombok")
    void shouldTestLombok() {
        ForgotPasswordRequest request = new ForgotPasswordRequest();
        request.setEmail("test@test.fr");
        assertThat(request.getEmail()).isEqualTo("test@test.fr");
    }
}