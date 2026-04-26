package com.SinistraPro.domain.exposition.dto.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ResetPasswordRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }



    @Test
    void resetPasswordRequest_champsValides_aucuneViolation() {
        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setEmail("client@test.com");
        request.setCode("123456");
        request.setNouveauMotDePasse("motDePasse123");

        Set<ConstraintViolation<ResetPasswordRequest>> violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }



    @Test
    void resetPasswordRequest_emailVide_leveViolation() {
        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setEmail("");
        request.setCode("123456");
        request.setNouveauMotDePasse("motDePasse123");

        Set<ConstraintViolation<ResetPasswordRequest>> violations = validator.validate(request);

        assertThat(violations).isNotEmpty();
        assertThat(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email"))).isTrue();
    }

    @Test
    void resetPasswordRequest_emailNull_leveViolation() {
        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setEmail(null);
        request.setCode("123456");
        request.setNouveauMotDePasse("motDePasse123");

        // WHEN
        Set<ConstraintViolation<ResetPasswordRequest>> violations = validator.validate(request);

        // THEN
        assertThat(violations).isNotEmpty();
        assertThat(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email"))).isTrue();
    }



    @Test
    void resetPasswordRequest_codeTropCourt_leveViolation() {
        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setEmail("client@test.com");
        request.setCode("123");  // 3 chiffres au lieu de 6
        request.setNouveauMotDePasse("motDePasse123");

        Set<ConstraintViolation<ResetPasswordRequest>> violations = validator.validate(request);

        assertThat(violations).isNotEmpty();
        assertThat(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("code"))).isTrue();
    }

    @Test
    void resetPasswordRequest_codeTropLong_leveViolation() {
        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setEmail("client@test.com");
        request.setCode("1234567");  // 7 chiffres au lieu de 6
        request.setNouveauMotDePasse("motDePasse123");

        Set<ConstraintViolation<ResetPasswordRequest>> violations = validator.validate(request);

        assertThat(violations).isNotEmpty();
        assertThat(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("code"))).isTrue();
    }

    @Test
    void resetPasswordRequest_codeVide_leveViolation() {
        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setEmail("client@test.com");
        request.setCode("");
        request.setNouveauMotDePasse("motDePasse123");

        // WHEN
        Set<ConstraintViolation<ResetPasswordRequest>> violations = validator.validate(request);

        // THEN
        assertThat(violations).isNotEmpty();
        assertThat(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("code"))).isTrue();
    }

    @Test
    void resetPasswordRequest_codeExactement6Chiffres_valide() {
        // GIVEN
        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setEmail("client@test.com");
        request.setCode("000000");
        request.setNouveauMotDePasse("motDePasse123");

        // WHEN
        Set<ConstraintViolation<ResetPasswordRequest>> violations = validator.validate(request);

        // THEN
        assertThat(violations).isEmpty();
    }



    @Test
    void resetPasswordRequest_nouveauMotDePasseTropCourt_leveViolation() {
        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setEmail("client@test.com");
        request.setCode("123456");
        request.setNouveauMotDePasse("abc");  // moins de 6 caractères

        Set<ConstraintViolation<ResetPasswordRequest>> violations = validator.validate(request);

        assertThat(violations).isNotEmpty();
        assertThat(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("nouveauMotDePasse"))).isTrue();
        assertThat(violations.stream()
                .filter(v -> v.getPropertyPath().toString().equals("nouveauMotDePasse"))
                .anyMatch(v -> v.getMessage().contains("6"))).isTrue();
    }

    @Test
    void resetPasswordRequest_nouveauMotDePasseVide_leveViolation() {
        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setEmail("client@test.com");
        request.setCode("123456");
        request.setNouveauMotDePasse("");

        Set<ConstraintViolation<ResetPasswordRequest>> violations = validator.validate(request);

        assertThat(violations).isNotEmpty();
        assertThat(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("nouveauMotDePasse"))).isTrue();
    }

    @Test
    void resetPasswordRequest_nouveauMotDePasseExactement6Caracteres_valide() {
        // GIVEN
        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setEmail("client@test.com");
        request.setCode("123456");
        request.setNouveauMotDePasse("abc123");  // exactement 6 caractères

        // WHEN
        Set<ConstraintViolation<ResetPasswordRequest>> violations = validator.validate(request);

        // THEN
        assertThat(violations).isEmpty();
    }



    @Test
    void resetPasswordRequest_gettersSetters_fonctionnentCorrectement() {
        // GIVEN
        ResetPasswordRequest request = new ResetPasswordRequest();

        // WHEN
        request.setEmail("test@test.com");
        request.setCode("654321");
        request.setNouveauMotDePasse("nouveauMdp");

        // THEN
        assertThat(request.getEmail()).isEqualTo("test@test.com");
        assertThat(request.getCode()).isEqualTo("654321");
        assertThat(request.getNouveauMotDePasse()).isEqualTo("nouveauMdp");
    }
}

















