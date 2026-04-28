package com.SinistraPro.Integration;

import com.SinistraPro.domain.application.service.PasswordResetService;
import com.SinistraPro.domain.exposition.controller.AuthController;
import com.SinistraPro.domain.exposition.dto.request.*;
import com.SinistraPro.domain.infrastructure.security.JwtService;
import com.SinistraPro.domain.infrastructure.security.SecurityConfig;
import com.SinistraPro.domain.model.Role;
import com.SinistraPro.domain.model.Utilisateur;
import com.SinistraPro.domain.port.out.UtilisateurRepositoryPort;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
@DisplayName("Tests d'intégration – AuthController")
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean private JwtService                jwtService;
    @MockBean private UtilisateurRepositoryPort utilisateurRepository;
    @MockBean private PasswordEncoder           passwordEncoder;
    @MockBean private PasswordResetService      passwordResetService;

    private ObjectMapper objectMapper;
    private Utilisateur  clientUser;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();

        clientUser = Utilisateur.builder()
                .id(1L).nom("Dupont").prenom("Jean")
                .email("client@test.com")
                .motDePasse("encodedPassword")
                .role(Role.CLIENT)
                .build();
    }



    @Test
    @DisplayName("POST /api/auth/register – 201 Created quand l'email est nouveau")
    void register_shouldReturn201_whenEmailIsNew() throws Exception {

        RegisterRequest request = new RegisterRequest();
        request.setNom("Dupont");
        request.setPrenom("Jean");
        request.setEmail("client@test.com");
        request.setMotDePasse("Password123!");
        request.setTelephone("0600000000");

        when(utilisateurRepository.existsByEmail("client@test.com")).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(utilisateurRepository.save(any(Utilisateur.class))).thenReturn(clientUser);
        when(jwtService.generateToken("client@test.com", "CLIENT")).thenReturn("jwt-token");

        mockMvc.perform(post("/api/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").value("jwt-token"))
                .andExpect(jsonPath("$.email").value("client@test.com"))
                .andExpect(jsonPath("$.role").value("CLIENT"));

        verify(utilisateurRepository, times(1)).save(any(Utilisateur.class));
    }

    @Test
    @DisplayName("POST /api/auth/register – 409 Conflict quand l'email existe déjà")
    void register_shouldReturn409_whenEmailAlreadyExists() throws Exception {

        RegisterRequest request = new RegisterRequest();
        request.setNom("Dupont");
        request.setPrenom("Jean");
        request.setEmail("client@test.com");
        request.setMotDePasse("Password123!");
        request.setTelephone("0600000000");

        when(utilisateurRepository.existsByEmail("client@test.com")).thenReturn(true);

        mockMvc.perform(post("/api/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());

        verify(utilisateurRepository, never()).save(any());
    }

    @Test
    @DisplayName("POST /api/auth/register – 400 Bad Request quand le body est invalide")
    void register_shouldReturn400_whenBodyIsInvalid() throws Exception {

        mockMvc.perform(post("/api/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }



    @Test
    @DisplayName("POST /api/auth/login – 200 OK avec credentials valides")
    void login_shouldReturn200_whenCredentialsAreValid() throws Exception {

        LoginRequest request = new LoginRequest();
        request.setEmail("client@test.com");
        request.setMotDePasse("Password123!");

        when(utilisateurRepository.findByEmail("client@test.com"))
                .thenReturn(Optional.of(clientUser));
        when(passwordEncoder.matches("Password123!", "encodedPassword")).thenReturn(true);
        when(jwtService.generateToken("client@test.com", "CLIENT")).thenReturn("jwt-token");

        mockMvc.perform(post("/api/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"))
                .andExpect(jsonPath("$.email").value("client@test.com"))
                .andExpect(jsonPath("$.role").value("CLIENT"));
    }

    @Test
    @DisplayName("POST /api/auth/login – 401 Unauthorized quand l'email est inconnu")
    void login_shouldReturn401_whenEmailNotFound() throws Exception {

        LoginRequest request = new LoginRequest();
        request.setEmail("inconnu@test.com");
        request.setMotDePasse("Password123!");

        when(utilisateurRepository.findByEmail("inconnu@test.com"))
                .thenReturn(Optional.empty());

        mockMvc.perform(post("/api/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /api/auth/login – 401 Unauthorized quand le mot de passe est incorrect")
    void login_shouldReturn401_whenPasswordIsWrong() throws Exception {

        LoginRequest request = new LoginRequest();
        request.setEmail("client@test.com");
        request.setMotDePasse("WrongPassword!");

        when(utilisateurRepository.findByEmail("client@test.com"))
                .thenReturn(Optional.of(clientUser));
        when(passwordEncoder.matches("WrongPassword!", "encodedPassword")).thenReturn(false);

        mockMvc.perform(post("/api/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }


    @Test
    @DisplayName("POST /api/auth/forgot-password – 200 OK quand l'email existe")
    void forgotPassword_shouldReturn200_whenEmailExists() throws Exception {

        ForgotPasswordRequest request = new ForgotPasswordRequest();
        request.setEmail("client@test.com");

        doNothing().when(passwordResetService).demanderReset("client@test.com");

        mockMvc.perform(post("/api/auth/forgot-password")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("client@test.com")));
    }

    @Test
    @DisplayName("POST /api/auth/forgot-password – 400 Bad Request quand l'email est inconnu")
    void forgotPassword_shouldReturn400_whenEmailUnknown() throws Exception {

        ForgotPasswordRequest request = new ForgotPasswordRequest();
        request.setEmail("inconnu@test.com");

        doThrow(new IllegalArgumentException("Email introuvable"))
                .when(passwordResetService).demanderReset("inconnu@test.com");

        mockMvc.perform(post("/api/auth/forgot-password")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Email introuvable"));
    }


    @Test
    @DisplayName("POST /api/auth/verify-code – 200 OK quand le code est valide")
    void verifyCode_shouldReturn200_whenCodeIsValid() throws Exception {

        VerifierCodeRequest request = new VerifierCodeRequest();
        request.setEmail("client@test.com");
        request.setCode("123456");

        doNothing().when(passwordResetService).verifierCode("client@test.com", "123456");

        mockMvc.perform(post("/api/auth/verify-code")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Code valide"));
    }

    @Test
    @DisplayName("POST /api/auth/verify-code – 400 Bad Request quand le code est expiré")
    void verifyCode_shouldReturn400_whenCodeIsExpired() throws Exception {

        VerifierCodeRequest request = new VerifierCodeRequest();
        request.setEmail("client@test.com");
        request.setCode("000000");

        doThrow(new IllegalArgumentException("Code expiré ou invalide"))
                .when(passwordResetService).verifierCode("client@test.com", "000000");

        mockMvc.perform(post("/api/auth/verify-code")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Code expiré ou invalide"));
    }



    @Test
    @DisplayName("POST /api/auth/reset-password – 200 OK quand la réinitialisation réussit")
    void resetPassword_shouldReturn200_whenSuccess() throws Exception {

        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setEmail("client@test.com");
        request.setCode("123456");
        request.setNouveauMotDePasse("NewPassword123!");

        doNothing().when(passwordResetService)
                .reinitialiserMotDePasse("client@test.com", "123456", "NewPassword123!");

        mockMvc.perform(post("/api/auth/reset-password")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Mot de passe réinitialisé avec succès"));
    }

    @Test
    @DisplayName("POST /api/auth/reset-password – 400 Bad Request quand le code est invalide")
    void resetPassword_shouldReturn400_whenCodeIsInvalid() throws Exception {

        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setEmail("client@test.com");
        request.setCode("999999");
        request.setNouveauMotDePasse("NewPassword123!");

        doThrow(new IllegalArgumentException("Code invalide"))
                .when(passwordResetService)
                .reinitialiserMotDePasse("client@test.com", "999999", "NewPassword123!");

        mockMvc.perform(post("/api/auth/reset-password")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Code invalide"));
    }
}