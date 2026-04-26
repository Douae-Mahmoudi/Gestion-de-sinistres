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

class RegisterRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Succès : Inscription valide avec tous les champs")
    void shouldBeValidWithFullData() {
        // Given
        RegisterRequest request = new RegisterRequest();
        request.setNom("Benani");
        request.setPrenom("Amine");
        request.setEmail("amine.ben@test.ma");
        request.setMotDePasse("StrongPass123");
        request.setTelephone("0661223344");

        // When
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Échec : Mot de passe trop court (@Size)")
    void shouldFailWhenPasswordIsTooShort() {
        // Given
        RegisterRequest request = createValidRegisterRequest();
        request.setMotDePasse("1234567"); // 7 caractères seulement

        // When
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).isNotEmpty();
        boolean hasSizeError = violations.stream()
                .anyMatch(v -> v.getMessage().equals("Le mot de passe doit contenir au moins 8 caractères"));
        assertThat(hasSizeError).isTrue();
    }

    @Test
    @DisplayName("Échec : Nom et Prénom vides (@NotBlank)")
    void shouldFailWhenNamesAreBlank() {

        RegisterRequest request = createValidRegisterRequest();
        request.setNom("");
        request.setPrenom("  ");

        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

        assertThat(violations).hasSizeGreaterThanOrEqualTo(2);
        assertThat(violations).anyMatch(v -> v.getMessage().contains("nom est obligatoire"));
        assertThat(violations).anyMatch(v -> v.getMessage().contains("prénom est obligatoire"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"wrong-email", "test@", "@domain"})
    @DisplayName("Échec : Format email invalide (@Email)")
    void shouldFailWithInvalidEmail(String invalidEmail) {
        RegisterRequest request = createValidRegisterRequest();
        request.setEmail(invalidEmail);

        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getMessage().equals("Format email invalide"));
    }

    @Test
    @DisplayName("Succès : Le téléphone est optionnel")
    void shouldBeValidWithoutTelephone() {
        RegisterRequest request = createValidRegisterRequest();
        request.setTelephone(null);

        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }


    private RegisterRequest createValidRegisterRequest() {
        RegisterRequest request = new RegisterRequest();
        request.setNom("Nom");
        request.setPrenom("Prenom");
        request.setEmail("valid@test.com");
        request.setMotDePasse("ValidPassword123");
        return request;
    }
}