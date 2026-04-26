package com.SinistraPro.domain.exposition.dto.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class RapportExpertRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Succès : Le rapport est valide avec tous les champs requis")
    void shouldBeValidWithCorrectData() {
        // Given
        RapportExpertRequest request = new RapportExpertRequest();
        request.setDescriptionDommages("Portière gauche enfoncée");
        request.setMontantEstime(new BigDecimal("1200.50"));
        request.setObservations("Nécessite un remplacement de pièce");

        // When
        Set<ConstraintViolation<RapportExpertRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Échec : La description est absente (@NotBlank)")
    void shouldFailWhenDescriptionIsBlank() {
        // Given
        RapportExpertRequest request = new RapportExpertRequest();
        request.setDescriptionDommages(""); // Vide
        request.setMontantEstime(new BigDecimal("500"));

        // When
        Set<ConstraintViolation<RapportExpertRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).isNotEmpty();
        boolean hasDescriptionError = violations.stream()
                .anyMatch(v -> v.getMessage().equals("La description des dommages est obligatoire"));
        assertThat(hasDescriptionError).isTrue();
    }

    @Test
    @DisplayName("Échec : Le montant est nul ou négatif (@DecimalMin)")
    void shouldFailWhenAmountIsZeroOrNegative() {
        // Given
        RapportExpertRequest request = new RapportExpertRequest();
        request.setDescriptionDommages("Dommages divers");
        request.setMontantEstime(new BigDecimal("0.0")); // Doit être > 0.0 (inclusive = false)

        // When
        Set<ConstraintViolation<RapportExpertRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).isNotEmpty();
        boolean hasAmountError = violations.stream()
                .anyMatch(v -> v.getMessage().equals("Le montant estimé doit être positif"));
        assertThat(hasAmountError).isTrue();
    }

    @Test
    @DisplayName("Échec : Le montant est absent (@NotNull)")
    void shouldFailWhenAmountIsNull() {
        // Given
        RapportExpertRequest request = new RapportExpertRequest();
        request.setDescriptionDommages("Dommages");
        request.setMontantEstime(null);

        // When
        Set<ConstraintViolation<RapportExpertRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).isNotEmpty();
        boolean hasNullError = violations.stream()
                .anyMatch(v -> v.getMessage().equals("Le montant estimé est obligatoire"));
        assertThat(hasNullError).isTrue();
    }

    @Test
    @DisplayName("Succès : Les observations sont optionnelles")
    void shouldBeValidWithoutObservations() {
        // Given
        RapportExpertRequest request = new RapportExpertRequest();
        request.setDescriptionDommages("Pare-brise fissuré");
        request.setMontantEstime(new BigDecimal("450.00"));
        request.setObservations(null); // Optionnel

        // When
        Set<ConstraintViolation<RapportExpertRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Vérifie les Getters/Setters de Lombok")
    void shouldTestLombok() {
        RapportExpertRequest request = new RapportExpertRequest();
        BigDecimal montant = new BigDecimal("1000");

        request.setMontantEstime(montant);
        request.setObservations("RAS");

        assertThat(request.getMontantEstime()).isEqualTo(montant);
        assertThat(request.getObservations()).isEqualTo("RAS");
    }
}