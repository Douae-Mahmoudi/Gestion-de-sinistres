package com.SinistraPro.domain.exposition.controller;

import com.SinistraPro.domain.application.service.PasswordResetService;
import com.SinistraPro.domain.exposition.dto.request.*;
import com.SinistraPro.domain.exposition.dto.response.AuthResponse;
import com.SinistraPro.domain.infrastructure.security.JwtService;
import com.SinistraPro.domain.model.Role;
import com.SinistraPro.domain.model.Utilisateur;
import com.SinistraPro.domain.port.out.UtilisateurRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock private PasswordResetService passwordResetService;
    @Mock private UtilisateurRepositoryPort utilisateurRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtService jwtService;

    @InjectMocks
    private AuthController authController;

    private Utilisateur utilisateur;

    @BeforeEach
    void setUp() {
        utilisateur = Utilisateur.builder()
                .id(1L)
                .nom("Doe")
                .prenom("John")
                .email("john@test.com")
                .motDePasse("encodedPassword")
                .telephone("0600000000")
                .role(Role.CLIENT)
                .dateCreation(LocalDateTime.now())
                .build();
    }

    @Test
    void register_emailDisponible_retourne201AvecToken() {
        RegisterRequest request = new RegisterRequest();
        request.setNom("Doe");
        request.setPrenom("John");
        request.setEmail("john@test.com");
        request.setMotDePasse("motDePasse123");
        request.setTelephone("0600000000");

        when(utilisateurRepository.existsByEmail("john@test.com")).thenReturn(false);
        when(passwordEncoder.encode("motDePasse123")).thenReturn("encodedPassword");
        when(utilisateurRepository.save(any())).thenReturn(utilisateur);
        when(jwtService.generateToken("john@test.com", "CLIENT")).thenReturn("jwt.token.test");

        ResponseEntity<AuthResponse> response = authController.register(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getToken()).isEqualTo("jwt.token.test");
        assertThat(response.getBody().getEmail()).isEqualTo("john@test.com");
        assertThat(response.getBody().getRole()).isEqualTo("CLIENT");
    }

    @Test
    void register_emailDejaUtilise_retourne409() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("john@test.com");
        request.setMotDePasse("motDePasse123");

        when(utilisateurRepository.existsByEmail("john@test.com")).thenReturn(true);

        ResponseEntity<AuthResponse> response = authController.register(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        verify(utilisateurRepository, never()).save(any());
    }

    @Test
    void login_credentialsValides_retourne200AvecToken() {
        LoginRequest request = new LoginRequest();
        request.setEmail("john@test.com");
        request.setMotDePasse("motDePasse123");

        when(utilisateurRepository.findByEmail("john@test.com")).thenReturn(Optional.of(utilisateur));
        when(passwordEncoder.matches("motDePasse123", "encodedPassword")).thenReturn(true);
        when(jwtService.generateToken("john@test.com", "CLIENT")).thenReturn("jwt.token.test");

        ResponseEntity<AuthResponse> response = authController.login(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getToken()).isEqualTo("jwt.token.test");
    }

    @Test
    void login_emailInexistant_retourne401() {
        LoginRequest request = new LoginRequest();
        request.setEmail("inconnu@test.com");
        request.setMotDePasse("motDePasse123");

        when(utilisateurRepository.findByEmail("inconnu@test.com")).thenReturn(Optional.empty());

        ResponseEntity<AuthResponse> response = authController.login(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        verify(jwtService, never()).generateToken(anyString(), anyString());
    }

    @Test
    void login_motDePasseIncorrect_retourne401() {
        LoginRequest request = new LoginRequest();
        request.setEmail("john@test.com");
        request.setMotDePasse("mauvaisMotDePasse");

        when(utilisateurRepository.findByEmail("john@test.com")).thenReturn(Optional.of(utilisateur));
        when(passwordEncoder.matches("mauvaisMotDePasse", "encodedPassword")).thenReturn(false);

        ResponseEntity<AuthResponse> response = authController.login(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        verify(jwtService, never()).generateToken(anyString(), anyString());
    }

    @Test
    void forgotPassword_emailExistant_retourne200() {
        ForgotPasswordRequest request = new ForgotPasswordRequest();
        request.setEmail("john@test.com");

        doNothing().when(passwordResetService).demanderReset("john@test.com");

        ResponseEntity<String> response = authController.forgotPassword(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("john@test.com");
    }

    @Test
    void forgotPassword_emailInexistant_retourne400() {
        ForgotPasswordRequest request = new ForgotPasswordRequest();
        request.setEmail("inconnu@test.com");

        doThrow(new IllegalArgumentException("Aucun compte associé à cet email."))
                .when(passwordResetService).demanderReset("inconnu@test.com");

        ResponseEntity<String> response = authController.forgotPassword(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("Aucun compte");
    }

    @Test
    void verifyCode_codeValide_retourne200() {
        VerifierCodeRequest request = new VerifierCodeRequest();
        request.setEmail("john@test.com");
        request.setCode("123456");

        doNothing().when(passwordResetService).verifierCode("john@test.com", "123456");

        ResponseEntity<String> response = authController.verifyCode(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Code valide");
    }

    @Test
    void verifyCode_codeInvalide_retourne400() {
        VerifierCodeRequest request = new VerifierCodeRequest();
        request.setEmail("john@test.com");
        request.setCode("000000");

        doThrow(new IllegalArgumentException("Code incorrect."))
                .when(passwordResetService).verifierCode("john@test.com", "000000");

        ResponseEntity<String> response = authController.verifyCode(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("Code incorrect.");
    }

    @Test
    void resetPassword_codeValide_retourne200() {
        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setEmail("john@test.com");
        request.setCode("123456");
        request.setNouveauMotDePasse("nouveauMdp123");

        doNothing().when(passwordResetService)
                .reinitialiserMotDePasse("john@test.com", "123456", "nouveauMdp123");

        ResponseEntity<String> response = authController.resetPassword(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("succès");
    }

    @Test
    void resetPassword_codeInvalide_retourne400() {
        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setEmail("john@test.com");
        request.setCode("000000");
        request.setNouveauMotDePasse("nouveauMdp123");

        doThrow(new IllegalArgumentException("Code incorrect."))
                .when(passwordResetService)
                .reinitialiserMotDePasse("john@test.com", "000000", "nouveauMdp123");

        ResponseEntity<String> response = authController.resetPassword(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("Code incorrect.");
    }
}
