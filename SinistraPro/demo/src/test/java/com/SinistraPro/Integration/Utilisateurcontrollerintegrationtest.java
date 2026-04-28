package com.SinistraPro.Integration;

import com.SinistraPro.domain.exposition.controller.UtilisateurController;
import com.SinistraPro.domain.exposition.dto.request.ChangePasswordRequest;
import com.SinistraPro.domain.exposition.dto.request.UpdateProfilRequest;
import com.SinistraPro.domain.infrastructure.security.JwtService;
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
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UtilisateurController.class)
@DisplayName("Tests d'intégration – UtilisateurController")
class UtilisateurControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean private JwtService                jwtService;
    @MockBean private UtilisateurRepositoryPort utilisateurRepository;
    @MockBean private PasswordEncoder           passwordEncoder;

    private ObjectMapper objectMapper;
    private Utilisateur  clientUser;
    private Utilisateur  expertUser;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());

        clientUser = Utilisateur.builder()
                .id(1L).nom("Dupont").prenom("Jean")
                .email("client@test.com")
                .telephone("0600000001")
                .adresse("12 rue de la Paix, Casablanca")
                .motDePasse("encodedOldPassword")
                .role(Role.CLIENT)
                .dateCreation(LocalDateTime.now())
                .build();

        expertUser = Utilisateur.builder()
                .id(3L).nom("Expert").prenom("Karim")
                .email("expert@test.com")
                .telephone("0600000003")
                .motDePasse("encodedExpertPassword")
                .role(Role.EXPERT)
                .dateCreation(LocalDateTime.now())
                .build();
    }


    @Test
    @WithMockUser(username = "client@test.com", roles = "CLIENT")
    @DisplayName("GET /api/utilisateurs/me – 200 OK avec le profil du client connecté")
    void getMonProfil_shouldReturn200_forConnectedUser() throws Exception {

        when(utilisateurRepository.findByEmail("client@test.com"))
                .thenReturn(Optional.of(clientUser));

        mockMvc.perform(get("/api/utilisateurs/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nom").value("Dupont"))
                .andExpect(jsonPath("$.prenom").value("Jean"))
                .andExpect(jsonPath("$.email").value("client@test.com"))
                .andExpect(jsonPath("$.telephone").value("0600000001"))
                .andExpect(jsonPath("$.role").value("CLIENT"));
    }

    @Test
    @DisplayName("GET /api/utilisateurs/me – 401/403 quand non authentifié")
    void getMonProfil_shouldReturn4xx_whenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/utilisateurs/me"))
                .andExpect(status().is4xxClientError());
    }


    @Test
    @WithMockUser(username = "client@test.com", roles = "CLIENT")
    @DisplayName("PUT /api/utilisateurs/me – 200 OK quand le profil est mis à jour")
    void updateProfil_shouldReturn200_whenValid() throws Exception {

        UpdateProfilRequest request = new UpdateProfilRequest();
        request.setNom("Martin");
        request.setPrenom("Pierre");
        request.setTelephone("0611111111");
        request.setAdresse("5 avenue Hassan II, Rabat");

        Utilisateur updated = Utilisateur.builder()
                .id(1L).nom("Martin").prenom("Pierre")
                .email("client@test.com")
                .telephone("0611111111")
                .adresse("5 avenue Hassan II, Rabat")
                .role(Role.CLIENT)
                .dateCreation(LocalDateTime.now())
                .build();

        when(utilisateurRepository.findByEmail("client@test.com"))
                .thenReturn(Optional.of(clientUser));
        when(utilisateurRepository.save(any(Utilisateur.class)))
                .thenReturn(updated);

        mockMvc.perform(put("/api/utilisateurs/me")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nom").value("Martin"))
                .andExpect(jsonPath("$.prenom").value("Pierre"))
                .andExpect(jsonPath("$.telephone").value("0611111111"))
                .andExpect(jsonPath("$.adresse").value("5 avenue Hassan II, Rabat"));

        verify(utilisateurRepository, times(1)).save(any(Utilisateur.class));
    }

    @Test
    @WithMockUser(username = "client@test.com", roles = "CLIENT")
    @DisplayName("PUT /api/utilisateurs/me – 400 Bad Request quand le body est invalide")
    void updateProfil_shouldReturn400_whenBodyIsInvalid() throws Exception {

        mockMvc.perform(put("/api/utilisateurs/me")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }


    @Test
    @WithMockUser(username = "client@test.com", roles = "CLIENT")
    @DisplayName("PUT /api/utilisateurs/me/password – 200 OK quand l'ancien mot de passe est correct")
    void changePassword_shouldReturn200_whenOldPasswordMatches() throws Exception {

        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setAncienMotDePasse("OldPassword123!");
        request.setNouveauMotDePasse("NewPassword456!");

        when(utilisateurRepository.findByEmail("client@test.com"))
                .thenReturn(Optional.of(clientUser));
        when(passwordEncoder.matches("OldPassword123!", "encodedOldPassword"))
                .thenReturn(true);
        when(passwordEncoder.encode("NewPassword456!"))
                .thenReturn("encodedNewPassword");
        when(utilisateurRepository.save(any(Utilisateur.class)))
                .thenReturn(clientUser);

        mockMvc.perform(put("/api/utilisateurs/me/password")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Mot de passe modifié avec succès"));

        verify(utilisateurRepository, times(1)).save(any(Utilisateur.class));
    }

    @Test
    @WithMockUser(username = "client@test.com", roles = "CLIENT")
    @DisplayName("PUT /api/utilisateurs/me/password – 400 Bad Request quand l'ancien mot de passe est incorrect")
    void changePassword_shouldReturn400_whenOldPasswordIsWrong() throws Exception {

        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setAncienMotDePasse("WrongOldPassword!");
        request.setNouveauMotDePasse("NewPassword456!");

        when(utilisateurRepository.findByEmail("client@test.com"))
                .thenReturn(Optional.of(clientUser));
        when(passwordEncoder.matches("WrongOldPassword!", "encodedOldPassword"))
                .thenReturn(false);

        mockMvc.perform(put("/api/utilisateurs/me/password")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Ancien mot de passe incorrect"));

        verify(utilisateurRepository, never()).save(any());
    }


    @Test
    @WithMockUser(roles = "AGENT")
    @DisplayName("GET /api/utilisateurs/experts – 200 OK avec la liste des experts")
    void getExperts_shouldReturn200_withExpertList() throws Exception {

        when(utilisateurRepository.findByRole(Role.EXPERT))
                .thenReturn(List.of(expertUser));

        mockMvc.perform(get("/api/utilisateurs/experts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(3))
                .andExpect(jsonPath("$[0].nom").value("Expert"))
                .andExpect(jsonPath("$[0].prenom").value("Karim"))
                .andExpect(jsonPath("$[0].email").value("expert@test.com"))
                .andExpect(jsonPath("$[0].role").value("EXPERT"));
    }

    @Test
    @WithMockUser(roles = "AGENT")
    @DisplayName("GET /api/utilisateurs/experts – 200 OK avec liste vide quand aucun expert")
    void getExperts_shouldReturn200_withEmptyList_whenNoExperts() throws Exception {

        when(utilisateurRepository.findByRole(Role.EXPERT))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/utilisateurs/experts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("GET /api/utilisateurs/experts – 401/403 quand non authentifié")
    void getExperts_shouldReturn4xx_whenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/utilisateurs/experts"))
                .andExpect(status().is4xxClientError());
    }
}