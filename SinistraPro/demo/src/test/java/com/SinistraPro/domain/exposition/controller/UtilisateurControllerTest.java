package com.SinistraPro.domain.exposition.controller;

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
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UtilisateurController.class)
@AutoConfigureMockMvc
class UtilisateurControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UtilisateurRepositoryPort utilisateurRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private JwtService jwtService; // Indispensable pour que le contexte démarre

    private Utilisateur mockUser;

    @BeforeEach
    void setUp() {
        mockUser = Utilisateur.builder()
                .id(1L)
                .email("test@sinistra.pro")
                .nom("Nom")
                .prenom("Prenom")
                .role(Role.CLIENT)
                .build();
    }

    @Test
    @WithMockUser(username = "test@sinistra.pro")
    @DisplayName("GET /api/utilisateurs/me - Succès")
    void getMonProfil_ShouldReturnUser() throws Exception {
        when(utilisateurRepository.findByEmail("test@sinistra.pro")).thenReturn(Optional.of(mockUser));

        mockMvc.perform(get("/api/utilisateurs/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@sinistra.pro"))
                .andExpect(jsonPath("$.nom").value("Nom"));
    }

    @Test
    @WithMockUser(username = "test@sinistra.pro")
    @DisplayName("PUT /api/utilisateurs/me - Succès")
    void updateProfil_ShouldReturnUpdatedUser() throws Exception {
        UpdateProfilRequest request = new UpdateProfilRequest();
        request.setNom("NouveauNom");
        request.setPrenom("NouveauPrenom");
        request.setTelephone("0600000000");
        request.setAdresse("Casablanca");

        when(utilisateurRepository.findByEmail("test@sinistra.pro")).thenReturn(Optional.of(mockUser));
        when(utilisateurRepository.save(any(Utilisateur.class))).thenReturn(mockUser);

        mockMvc.perform(put("/api/utilisateurs/me")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nom").value("NouveauNom"));
    }
}