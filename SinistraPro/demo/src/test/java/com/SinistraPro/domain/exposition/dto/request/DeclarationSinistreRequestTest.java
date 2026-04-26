package com.SinistraPro.domain.exposition.dto.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class DeclarationSinistreRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Succès : La déclaration est valide avec tous les champs corrects")
    void shouldBeValidWithCorrectData() {
        // Given
        DeclarationSinistreRequest request = new DeclarationSinistreRequest();
        request.setTypeSinistre("ACCIDENT_AUTO");
        request.setDescription("Collision arrière au feu rouge");
        request.setDateIncident(LocalDate.now().minusDays(1));
        request.setLieuIncident("Casablanca, Boulevard Anfa");
        request.setNumeroPolicAssurance("POL-998877");
        request.setNumeroConstatAmiable("CST-112233");

        // When
        Set<ConstraintViolation<DeclarationSinistreRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Échec : La date d'incident est dans le futur (@PastOrPresent)")
    void shouldFailWhenDateIsInFuture() {
        // Given
        DeclarationSinistreRequest request = createValidRequest();
        request.setDateIncident(LocalDate.now().plusDays(5));

        // When
        Set<ConstraintViolation<DeclarationSinistreRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("La date d'incident ne peut pas être dans le futur");
    }

    @Test
    @DisplayName("Échec : Les champs obligatoires sont vides (@NotBlank)")
    void shouldFailWhenRequiredFieldsAreBlank() {
        DeclarationSinistreRequest request = new DeclarationSinistreRequest();

        Set<ConstraintViolation<DeclarationSinistreRequest>> violations = validator.validate(request);


        assertThat(violations).hasSize(6);
    }

    @Test
    @DisplayName("Vérifie les Getters et Setters de Lombok")
    void shouldTestLombokAccessors() {
        DeclarationSinistreRequest request = new DeclarationSinistreRequest();
        String police = "POL-123";

        request.setNumeroPolicAssurance(police);

        assertThat(request.getNumeroPolicAssurance()).isEqualTo(police);
    }


    private DeclarationSinistreRequest createValidRequest() {
        DeclarationSinistreRequest request = new DeclarationSinistreRequest();
        request.setTypeSinistre("TEST");
        request.setDescription("TEST DESC");
        request.setDateIncident(LocalDate.now());
        request.setLieuIncident("TEST LIEU");
        request.setNumeroPolicAssurance("POL-TEST");
        request.setNumeroConstatAmiable("CST-TEST");
        return request;
    }
}