package com.SinistraPro.domain.exposition.dto.request;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class AffectationRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Succès : Le DTO est valide avec tous les champs")
    void shouldBeValidWhenAllFieldsAreCorrect() {
        AffectationRequest request = new AffectationRequest();
        request.setExpertId(123L);
        request.setCommentaireAgent("Affectation prioritaire");

        Set<ConstraintViolation<AffectationRequest>> violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Succès : Le DTO est valide même sans commentaire (optionnel)")
    void shouldBeValidWithoutComment() {
        // Given
        AffectationRequest request = new AffectationRequest();
        request.setExpertId(123L);
        request.setCommentaireAgent(null);

        // When
        Set<ConstraintViolation<AffectationRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Échec : expertId est obligatoire (@NotNull)")
    void shouldFailWhenExpertIdIsNull() {
        // Given
        AffectationRequest request = new AffectationRequest();
        request.setExpertId(null);
        request.setCommentaireAgent("Note");

        // When
        Set<ConstraintViolation<AffectationRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("L'identifiant de l'expert est obligatoire");
    }

    @Test
    @DisplayName("Vérifie que les getters et setters Lombok fonctionnent")
    void shouldTestLombokGettersSetters() {
        // Given
        AffectationRequest request = new AffectationRequest();

        // When
        request.setExpertId(5L);
        request.setCommentaireAgent("Test");

        // Then
        assertThat(request.getExpertId()).isEqualTo(5L);
        assertThat(request.getCommentaireAgent()).isEqualTo("Test");
    }
}