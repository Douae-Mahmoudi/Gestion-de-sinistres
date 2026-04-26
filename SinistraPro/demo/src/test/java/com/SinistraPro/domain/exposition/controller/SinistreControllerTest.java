package com.SinistraPro.domain.exposition.controller;

import com.SinistraPro.domain.application.service.NotificationService;
import com.SinistraPro.domain.exposition.dto.request.DeclarationSinistreRequest;
import com.SinistraPro.domain.exposition.dto.response.SinistreResponse;
import com.SinistraPro.domain.exposition.mapper.SinistreDtoMapper;
import com.SinistraPro.domain.model.*;
import com.SinistraPro.domain.port.in.*;
import com.SinistraPro.domain.port.out.SinistreRepositoryPort;
import com.SinistraPro.domain.port.out.UtilisateurRepositoryPort;
import com.SinistraPro.domain.infrastructure.security.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SinistreController.class)
@AutoConfigureMockMvc
class SinistreControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean private JwtService jwtService;
    @MockBean private DeclarerSinistreUseCase declarerUseCase;
    @MockBean private AffecterExpertUseCase affecterUseCase;
    @MockBean private SoumettreRapportUseCase soumettreRapportUseCase;
    @MockBean private ApprouverSinistreUseCase approuverUseCase;
    @MockBean private RejeterSinistreUseCase rejeterUseCase;
    @MockBean private CloturerSinistreUseCase cloturerUseCase;
    @MockBean private SinistreRepositoryPort sinistreRepository;
    @MockBean private UtilisateurRepositoryPort utilisateurRepository;
    @MockBean private SinistreDtoMapper dtoMapper;
    @MockBean private NotificationService notificationService;

    private Utilisateur mockUser;

    @BeforeEach
    void setUp() {
        mockUser = Utilisateur.builder()
                .id(1L)
                .email("test@sinistra.pro")
                .role(Role.CLIENT)
                .build();
    }

    @Test
    @WithMockUser(username = "test@sinistra.pro")
    @DisplayName("POST /api/sinistres - Succès")
    void declarer_ShouldReturnCreated() throws Exception {
        // Préparation d'un DTO complet pour passer la validation @NotBlank/@NotNull
        DeclarationSinistreRequest request = new DeclarationSinistreRequest();
        request.setTypeSinistre("ACCIDENT_AUTO");
        request.setDescription("Choc frontal");
        request.setDateIncident(LocalDate.now());
        request.setLieuIncident("Casablanca");
        request.setNumeroPolicAssurance("POL-12345");
        request.setNumeroConstatAmiable("CONSTAT-999");

        Sinistre createdSinistre = Sinistre.builder().id(100L).build();
        SinistreResponse response = SinistreResponse.builder().id(100L).build();

        // Mocks
        when(utilisateurRepository.findByEmail("test@sinistra.pro")).thenReturn(Optional.of(mockUser));
        when(declarerUseCase.declarer(any(Sinistre.class))).thenReturn(createdSinistre);
        when(dtoMapper.toResponse(any(Sinistre.class))).thenReturn(response);

        mockMvc.perform(post("/api/sinistres")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(100L));
    }

    @Test
    @WithMockUser(username = "test@sinistra.pro")
    @DisplayName("POST /api/sinistres - Erreur Utilisateur non trouvé")
    void declarer_UserNotFound() throws Exception {
        DeclarationSinistreRequest request = new DeclarationSinistreRequest();
        request.setTypeSinistre("ACCIDENT_AUTO");
        request.setDescription("Choc frontal");
        request.setDateIncident(LocalDate.now());
        request.setLieuIncident("Casablanca");
        request.setNumeroPolicAssurance("POL-12345");
        request.setNumeroConstatAmiable("CONSTAT-999");

        when(utilisateurRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        ServletException exception = assertThrows(ServletException.class, () -> {
            mockMvc.perform(post("/api/sinistres")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)));
        });

        assertTrue(exception.getCause().getMessage().contains("Utilisateur non trouvé"));
    }
}