package com.SinistraPro.domain.exposition.dto.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class DecisionRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Succès : Le DTO est valide avec des données complètes")
    void shouldBeValidWithAllFields() {
        DecisionRequest request = new DecisionRequest();
        request.setMontantFinal(new BigDecimal("1500.00"));
        request.setMotif("Dossier conforme aux règles d'indemnisation");
        request.setNumeroVirement("VIR-88901");
        request.setDatePaiement(LocalDate.now());

        Set<ConstraintViolation<DecisionRequest>> violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Échec : Le motif est vide ou blanc (@NotBlank)")
    void shouldFailWhenMotifIsBlank() {
        DecisionRequest request = new DecisionRequest();
        request.setMontantFinal(new BigDecimal("100.00"));
        request.setMotif("   ");


        Set<ConstraintViolation<DecisionRequest>> violations = validator.validate(request);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("Le motif est obligatoire");
    }

    @Test
    @DisplayName("Échec : Le montant est négatif ou nul (@DecimalMin)")
    void shouldFailWhenMontantIsNegativeOrZero() {
        DecisionRequest request = new DecisionRequest();
        request.setMotif("Motif valide");
        request.setMontantFinal(new BigDecimal("0.0"));

        Set<ConstraintViolation<DecisionRequest>> violations = validator.validate(request);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("Le montant doit être positif");
    }

    @Test
    @DisplayName("Succès : Les champs de paiement sont optionnels au niveau validation")
    void shouldBeValidWithoutPaymentDetails() {
        // Given
        DecisionRequest request = new DecisionRequest();
        request.setMontantFinal(new BigDecimal("500.00"));
        request.setMotif("Accord de principe");

        // When
        Set<ConstraintViolation<DecisionRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Vérifie l'intégrité des Getters/Setters Lombok")
    void shouldTestLombokFunctionality() {
        DecisionRequest request = new DecisionRequest();
        BigDecimal montant = new BigDecimal("2000.50");
        LocalDate date = LocalDate.of(2026, 4, 20);

        request.setMontantFinal(montant);
        request.setNumeroVirement("TEST-123");
        request.setDatePaiement(date);

        assertThat(request.getMontantFinal()).isEqualByComparingTo(montant);
        assertThat(request.getNumeroVirement()).isEqualTo("TEST-123");
        assertThat(request.getDatePaiement()).isEqualTo(date);
    }
}